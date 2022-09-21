(ns beagle.engine
  (:require
   [beagle.laws :as beagle.laws]
   [beagle.policies :as beagle.policies]
   [clojure.pprint :as pprint]
   [cognitect.aws.client.api :as aws]))

(defn may-i-trust-those-policies? [user-policies laws iam-client]
  (let [simulate-custom-policy-request (-> laws
                                           beagle.laws/->simulate-custom-policy
                                           (assoc :PolicyInputList user-policies))
        simulate-custom-policy-response (aws/invoke iam-client {:op :SimulateCustomPolicy :request simulate-custom-policy-request})
        broken-laws (->> simulate-custom-policy-response
                         :EvaluationResults
                         (filter #(not= (:EvalDecision %) "implicitDeny")))]
    {:status (empty? broken-laws) :broken-laws broken-laws}))

(defn cli [{:keys [policy laws]}]
  (if (and policy laws)
    (let [user-policies (beagle.policies/read policy)
          laws (beagle.laws/read laws)
          iam-client (aws/client {:api :iam})
          {:keys [status broken-laws]} (may-i-trust-those-policies? user-policies laws iam-client)]
      (if status
        (println "The provided policies are worth my trust.")
        (do
          (println "The provided policies are breaking my laws.")
          (println "Broken laws")
          (run! pprint/pprint broken-laws)
          (System/exit 1))))
    (println "You need to provide at least one policy and one directory with laws.")))
