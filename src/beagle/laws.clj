(ns beagle.laws
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]))

(defn ^:private law->simulate-custom-policy [simulate-custom-policy-request policy]
  (let [statements (:Statement policy)]
    (reduce (fn [simulate-custom-policy-request statement]
              (-> simulate-custom-policy-request
                  (update :ActionNames concat (:Action statement))
                  (update :ResourceArns concat (:Resource statement))))
            simulate-custom-policy-request statements)))

(defn read [path]
  (->> path
       io/file
       file-seq
       (filter #(.isFile %))
       (map (comp #(json/read-str % :key-fn keyword) slurp))))

(defn ->simulate-custom-policy [policies]
  (reduce law->simulate-custom-policy {:ActionNames [] :ResourceArns []} policies))
