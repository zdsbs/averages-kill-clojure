(ns averages-kill-clojure.test.sim
  (:use [averages-kill-clojure.sim] :reload)
  (:use clojure.test)
  (:use midje.sweet))


(def tier-with-work-order
    {:work [(work-order 1)]
         :free-workers [worker]
         :working []
         :completed-work []})

(fact (expected-number-of-work-orders {:work [(work-order 1) (work-order 2)]}) => 2)

(fact (sim-finished? {:completed-work [(work-order 1)]} 1) => true)

(fact 
  (free-workers? {:free-workers [.blah.]}) => true
  (free-workers? {:free-workers []}) => false)


(fact (outstanding-work? tier-with-work-order) => true)

(fact (start-agents-working {:work [(work-order 1) (work-order 1)]
                             :free-workers [worker worker]
                             :working []
                             :completed-work []}) =>
                            {:work []
                             :free-workers []
                             :working [[0 (work-order 1)] [0 (work-order 1)]]}
      (start-agents-working {:work [(work-order 1)]
                              :free-workers [worker]
                              :working [0 (work-order 1)]
                              :completed-work []}) =>
                            {:work []
                             :free-workers []
                             :working [[0 (work-order 1)]]})


(fact (inc-work [0 (work-order 1)]) => [1 (work-order 1)])

(fact (unfinished-work? [1 1]) => false
      (unfinished-work? [0 1]) => true)

(fact (filter unfinished-work? (:working {:working [[1 1]]})) => [])

(fact 
      (last-tier-work tier-with-work-order) =>     
      {:work []
       :free-workers []
       :working [[0 (work-order 1)]]
       :completed-work []}
      (last-tier-work (last-tier-work tier-with-work-order))
                                                => {:work []
                                                   :free-workers []
                                                   :working [[1 (work-order 1)]]
                                                   :completed-work []}
      (last-tier-work (last-tier-work (last-tier-work tier-with-work-order))) 
                                                => {:work []
                                                   :free-workers []
                                                   :working []
                                                   :completed-work [(work-order 1)]})

(fact "when last tier completed-work == expected number of work we're done"
      (run-sim [
                {:work [(work-order 1) (work-order 1)]
                 :completed-work []}
                {:work []
                 :completed-work [(work-order 1) (work-order 1)]}
                ]) => 0)




