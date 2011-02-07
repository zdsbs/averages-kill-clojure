(ns averages-kill-clojure.test.big
  (:use [averages-kill-clojure.sim] :reload)
  (:use clojure.test)
  (:use midje.sweet))


(def tier-with-work-order
    {:work [(work-order 1)]
         :free-workers [worker]
         :working []
         :completed-work []})


(fact "big" (run-sim [tier-with-work-order]) => 3)


