(ns beagle.engine
  (:require [cognitect.aws.client.api :as aws]
            [clojure.data.json :as json] [clojure.java.io :as io]))

(def iam (aws/client {:api :iam}))

(defn read-laws [path]
  (->> path
       io/file
       file-seq
       (filter #(.isFile %))
       (map (comp #(json/read-str % :key-fn keyword) slurp))))

(defn read-policies [policy]
  (map slurp policy))

(defn parse-forbidden-policy [simulate-custom-policy-request policy]
  (let [statements (:Statement policy)]
    (reduce (fn [simulate-custom-policy-request statement]
              (-> simulate-custom-policy-request
                  (update :ActionNames concat (:Action statement))
                  (update :ResourceArns concat (:Resource statement))))
            simulate-custom-policy-request statements)))

(defn parse-forbidden-policies [policies]
  (reduce parse-forbidden-policy {:ActionNames [] :ResourceArns []} policies))

(defn may-i-trust-this-policy? [simulate-custom-policy-request user-policies]
  (let [iam-request {:op :SimulateCustomPolicy :request (assoc simulate-custom-policy-request :PolicyInputList user-policies)}]
    (->> iam-request
         (aws/invoke iam)
         :EvaluationResults
         (filter #(not= (:EvalDecision %) "implicitDeny"))
         empty?)))

(defn main [{:keys [policy laws]}]
  (if (and policy laws)
    (let [user-policies (read-policies policy)
          forbidden-policies (read-laws laws)
          simulate-custom-policy-request (parse-forbidden-policies forbidden-policies)]
      (if (may-i-trust-this-policy? simulate-custom-policy-request user-policies)
        (println "You can trust in it.")
        (do
          (println "You can not trust in it.")
          (System/exit 1))))
    (println "You need to provide at least one policy and one directory with laws.")))
