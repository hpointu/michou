(ns hpointu.michou
  (:gen-class)
  (:require [clojure.spec.alpha :as s]))



(s/def ::layer-name
  string?)

(s/def ::filename
  string?)

(s/def ::include (s/cat :file ::filename :name ::layer-name))

(s/def ::file-op-method #{:copy :link})

(s/def ::file-op
  (s/cat :source ::filename
         :method ::file-op-method
         :origin ::filename))

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


(s/conform ::file-op ["toto" :link "tata"])
(s/conform ::origin [:origin "test"
                     ["toto" :link "tata"]
                     ["tutu" :link "Truc"]])

(s/conform ::layer
           [:layer "test"
            [:destination "there"
             [:origin "input" ["toto" :link "tata"]]]])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
