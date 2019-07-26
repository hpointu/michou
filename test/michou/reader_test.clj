(ns michou.reader-test
  (:require [clojure.test :refer :all]
            [hpointu.michou.core :as mc]
            [hpointu.michou.reader :as rd]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))


(deftest indent-level
  (testing "no indent is 0"
    (is (= 0 (rd/indent-level "a line"))))
  (testing "some random indent"
    (is (= 4 (rd/indent-level "    Lol it's funny")))))


(deftest detect-indent-size
  (testing "empty lines (just spaces) don't count"
    (let [lines ["first line"
                 "second"
                 "       "
                 "hop"
                 "  Hey I'm indented"
                 "    Me too but not first"]]
      (is (= 2 (rd/detect-indent-size lines))))))


(deftest real-line?
  (testing "lines wich are just spaces are useless"
    (is (not (rd/real-line? "   ")))
    (is (not (rd/real-line? "")))
    (is (rd/real-line? "  a word   "))))


(deftest depth-seq
  (testing "extract sequence of depth"
    (let [lines ["first"
                 "  second"
                 "  again"
                 "    child"
                 "  bam"]
          expected [0 1 1 2 1]]
      (is (= expected (rd/depth-seq lines))))))

(deftest build-ajs
  (testing "Trivial adjacency list"
    (let [depths [[0 0] [1 1] [2 1] [3 2] [4 0] [5 1]]
          adj-map (rd/build-adj depths)]
      (is (= (set (get adj-map nil)) #{0 4}))
      (is (= (set (get adj-map 0)) #{1 2}))
      (is (= (set (get adj-map 1)) #{}))
      (is (= (set (get adj-map 2)) #{3}))
      (is (= (set (get adj-map 3)) #{}))
      (is (= (set (get adj-map 4)) #{5}))
      (is (= (set (get adj-map 5)) #{})))))
