(ns hpointu.michou
  (:require [hpointu.michou.reader :as rd]))

(defn -main
  "Run michou"
  [& args]
  (if-let [filename (first args)]
    (do
      (println "Using" filename)
      (println (rd/read-file filename)))
    (println "Hmmm, no file specified...")))
