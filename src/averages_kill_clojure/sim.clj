(ns averages-kill-clojure.sim)

(def worker 
  :worker)

(defn work-order [size]
  size)

(defn expected-number-of-work-orders [first-tier]
  (count (:work first-tier)))

(defn sim-finished? [last-tier expected-completed]
  (= (count (:completed-work last-tier)) expected-completed))

(defn free-workers? [tier]
  (not (empty? (:free-workers tier))))


(defn outstanding-work? [tier]
  (not (empty? (:work tier))))


(defn start-one-agents-work [tier]
    (let [free-workers (rest (:free-workers tier))
          work (rest (:work tier))
          working (conj (:working tier) [0 (first (:work tier))])]

          { :work work 
            :free-workers free-workers 
            :working working}))


(defn start-agents-working [tier]
  (loop [tier (assoc tier :working [])]
    (if (not (and (free-workers? tier) (outstanding-work? tier)))
      tier
      (recur (start-one-agents-work tier)))))



(defn inc-work [working]
  [(inc (first working)) (second working)])


(defn unfinished-work? [work-in-progress]
  (< (first work-in-progress) (second work-in-progress)))

(defn finished-work? [work-in-progress]
  (= (first work-in-progress) (second work-in-progress)))

(defn agents-work [tier]
  (assoc tier :working 
         (map inc-work 
              (filter unfinished-work? (:working tier)))))


(defn completed-work [tier]
  {:completed-work (map second (filter finished-work? (:working tier)))})

(defn free-up-workers [tier]
  tier)

(defn last-tier-work [tier]
   (let [started-work (start-agents-working tier)
         did-some-work (agents-work tier)
         completed-some-work (completed-work tier)
         freed-up-workers (free-up-workers tier)]
          (assoc tier :working (vec (concat (:working started-work) (:working did-some-work)))
                      :work (:work started-work)
                      :free-workers (:free-workers started-work)
                      :completed-work (:completed-work completed-some-work))))


(defn do-work [tiers]
  (println "i" tiers)
  (let [update (last-tier-work (first tiers))]
    (println "u" update)
    [update]))

(defn run-sim [orig-tiers]
  (println "Starting sim\n" orig-tiers)
  (loop [tiers orig-tiers cur-time 0]
    (println cur-time)
    (if (or
          (= 100 cur-time)
          (sim-finished? (last tiers) (expected-number-of-work-orders (first orig-tiers))))
      cur-time
      (recur (do-work tiers) (inc cur-time)))))

