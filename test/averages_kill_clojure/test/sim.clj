(ns averages-kill-clojure.test.sim
  (:use [averages-kill-clojure.sim] :reload)
  (:use clojure.test)
  (:use midje.sweet))

(defn worker [])

(defn work-order [size])


(def tier-with-work-order
  {:work [(work-order 1)]
    :free-workers [worker]
    :working []
    :completed-work []})

(defn expected-number-of-work-orders [first-tier]
  (count (:work first-tier)))

(fact (expected-number-of-work-orders {:work [(work-order 1) (work-order 2)]}) => 2)

(defn finished? [last-tier expected-completed]
  (= (count (:completed-work last-tier)) expected-completed))

(fact (finished? {:completed-work [(work-order 1)]} 1) => true)

(defn free-workers? [tier]
  (not (empty? (:free-workers tier))))

(fact 
  (free-workers? {:free-workers [.blah.]}) => true
  (free-workers? {:free-workers []}) => false)

(defn outstanding-work? [tier]
  (not (empty? (:work tier))))

(fact (outstanding-work? tier-with-work-order) => true)



(defn start-one-agents-work [tier]
    (let [free-workers (rest (:free-workers tier))
          work (rest (:work tier))
          working (conj (:working tier) [0 (first (:work tier))])]

          (assoc tier :work work 
                      :free-workers free-workers 
                      :working working)))


(defn start-agents-working [tier]
  (loop [tier tier]
    (if (not (and (free-workers? tier) (outstanding-work? tier)))
      tier
      (recur (start-one-agents-work tier)))))

(fact (start-agents-working {:work [(work-order 1) (work-order 1)]
                             :free-workers [worker worker]
                             :working []
                             :completed-work []}) =>
                            {:new-work []
                             :new-free-workers []
                             :new-working [[0 (work-order 1)] [0 (work-order 1)]]})



(defn inc-work [working]
  [(inc (first working)) (second working)])

(fact (inc-work [0 (work-order 1)]) => [1 (work-order 1)])

(defn agents-work [tier]
  (assoc tier :working (map inc-work (:working tier))))

(fact (agents-work {:working [[0 (work-order 1)] [1 (work-order 2)]]}) =>
                  {:working [[1 (work-order 1)] [2 (work-order 2)]]})


(defn reconsile [tierA tierB]
    (assoc tierA :working (vec (concat (:working tierA) (:working tierB)))
                 :work (vec (concat (:work tierA) (:work tierB)))
                 :free-workers (vec (concat (:free-workers tierA) (:free-workers tierB)))
                 :completed-work (vec (concat (:completed-work tierA) (:completed-work tierB)))))

(defn last-tier-work [tier]
   (let [started-work (start-agents-working tier)
         did-some-work (agents-work tier)]
          (assoc started-work :working (vec (concat (:working started-work) (:working did-some-work))))))

(fact 
      (last-tier-work tier-with-work-order) =>     
      {:work []
       :free-workers []
       :working [[0 (work-order 1)]]
       :completed-work []}
      (last-tier-work (last-tier-work tier-with-work-order))
                                                => {:work []
                                                   :free-workers []
                                                   :working [1 (work-order 1)]
                                                   :completed-work []}
  )
;      (last-tier-work (last-tier-work (last-tier-work tier-with-work-order))) 
;                                                => {:work []
;                                                   :free-workers []
;                                                   :working []
;                                                   :completed-work [(work-order 1)]}
;      (last-tier-work (last-tier-work (last-tier-work (last-tier-work tier-with-work-order)))) 
;                                                => {:work []
;                                                   :free-workers [worker]
;                                                   :working []
;                                                   :completed-work []})

(defn do-work [tiers] 
  [])

(defn run-sim [tiers]
  (loop [tiers tiers cur-time 0]
    (if (finished? (last tiers) (expected-number-of-work-orders (first tiers)))
      cur-time
      (recur (do-work tiers) (inc cur-time)))))

(fact "when last tier completed-work == expected number of work we're done"
      (run-sim [
                {:work [(work-order 1) (work-order 1)]
                 :completed-work []}
                {:work []
                 :completed-work [(work-order 1) (work-order 1)]}
                ]) => 0)


(defn complete [tier]
  (assoc tier :completed-work (:work tier)))

(fact (run-sim [tier-with-work-order]) => 1
      (provided (do-work [tier-with-work-order]) => (complete tier-with-work-order)))


(fact (run-sim [tier-with-work-order]) => 3
      (provided (finished? tier-with-work-order 1) => true))


