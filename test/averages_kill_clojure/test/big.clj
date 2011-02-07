(ns averages-kill-clojure.test.big
  (:use [averages-kill-clojure.sim] :reload)
  (:use clojure.test)
  (:use midje.sweet))

(def empty-tier
  {:work []
   :free-workers []
   :working []
   :completed-work []})

(defn with [workers work]
  (assoc empty-tier :work work
         :free-workers workers))


(def tier-with-work-order
    {:work [(work-order 1)]
         :free-workers [worker]
         :working []
         :completed-work []})


(fact "big" (run-sim [(with [worker] [(work-order 1) (work-order 1)])]) => 3)


