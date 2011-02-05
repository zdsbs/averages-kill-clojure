(ns averages-kill-clojure.test.sim
  (:use [averages-kill-clojure.sim] :reload)
  (:use clojure.test)
  (:use midje.sweet))


(fact (inc 1) => 2)
(fact (inc 1) => 22)

