(ns hpointu.michou.core
  (:gen-class)
  (:require [clojure.spec.alpha :as s]))

(s/def ::layer-name
  (s/and string? not-empty))

(s/def ::filename
  (s/and string? not-empty))

(s/def ::include
  (s/cat :file ::filename
         :name ::layer-name))

(s/def ::file-op-method #{:copy :link})

(s/def ::file-op
  (s/cat :output ::filename
         :method ::file-op-method
         :source ::filename))

(s/def ::layer
  (s/cat :type #{:layer}
         :name ::layer-name
         :children (s/* (s/or :destination ::destination
                              :include ::include))))

(s/def ::destination
  (s/cat :type #{:destination}
         :dirname ::filename
         :children (s/* (s/or :origin ::origin
                              :file-op ::file-op
                              :destination ::destination))))

(s/def ::origin
  (s/cat :type #{:origin}
         :dirname ::filename
         :children (s/* (s/spec ::file-op))))

(s/exercise ::layer 2)
