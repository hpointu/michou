(ns hpointu.michou.reader
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))


(def indent-regex #"( *)(.*)")

(defn enumerate [coll]
  (map vector (iterate inc 0) coll))

(defn indent-level
  "How many spaces?"
  [line]
  (-> (re-matches indent-regex (string/trimr line)) second count))

(defn detect-indent-size
  "Detect the number of spaces used for one indentation level"
  [lines]
  (let [levels (map indent-level lines)]
    (first (drop-while zero? levels))))

(defn- line-depth
  "Depth of a line given the `indent-size` unit"
  [indent-size line]
  (let [indent (indent-level line)]
    (/ indent indent-size)))

(defn real-line?
  "get rid of empty lines while parsing"
  [line] (not-empty (string/trim line)))

(defn depth-seq
  "The sequence of depths of real lines"
  [lines] {:pre [(every? real-line? lines)]}
  (let [indent-size (detect-indent-size lines)
        depth (partial line-depth indent-size)]
    (map depth lines)))

(defn build-adj
  "Build and adjacency map from a sequence of indexed depths"
  ([adj-map path prev depths]
   (if-let [cur (first depths)]
     (let [[id depth] cur
           parent (peek path)
           parent-depth (count path)
           others (rest depths)]
       (if (< depth parent-depth)
         (build-adj adj-map (pop path) prev depths)
         (if (= depth parent-depth)
           (build-adj (update adj-map parent conj id) path id others)
           (build-adj (update adj-map prev conj id) (conj path prev) id others))))
     adj-map))

  ([depths] (build-adj {} [] nil depths)))

(defn ->node-type
  "From a list of tokens, find out type of node"
  [[ntype]]
  (case ntype
    "layer" :layer
    "into" :destination
    "from" :origin
    (throw (Exception. (str "Invalid instruction type: " ntype)))))

(defn node? [line tokens]
  (and (= \: (last line))
       (->node-type tokens)))

(defn ->op
  [op]
  (case op
    "<<" :copy
    "<-" :link
    :link))

(defn ->file-op
  ([source]
   [source :link source])
  ([dest op source]
   [dest (->op op) source]))

(defn- line->base-node
  [line]
  (let [clean-line (string/trim line)
        tokens (string/split clean-line #"[\s:]+")]
    (if-let [node-type (node? clean-line tokens)]
      (into [node-type] (rest tokens))
      (apply ->file-op tokens))))

(defn ->node
  [line children]
  (let [node (line->base-node line)]
    (into node children)))

(defn neighbours
  [adj-map node-id]
  (get adj-map node-id))

(defn build-tree
  [lines adj-map node]
  (let [->tree (partial build-tree lines adj-map)
        children (neighbours adj-map node)]
    (->node (get lines node) (map ->tree children))))

(defn read-file
  [filename]
  (with-open [rdr (io/reader filename)]
    (let [lines (vec (filter real-line? (line-seq rdr)))
          adj (build-adj (enumerate (depth-seq lines)))]
      (build-tree lines adj 0))))
