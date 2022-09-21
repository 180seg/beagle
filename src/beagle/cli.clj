(ns beagle.cli
  (:require [cli-matic.core :as climatic]
            [beagle.engine :as engine]))

(def CONFIGURATION
  {:app      {:command     "beagle"
              :description "Beagle is an AWS policy judge."
              :version     "1.0.0"}
   :commands [{:command     "check"
               :description "Check if one or more policies are breaking any rule."
               :opts        [{:option   "policy" :short "p"
                              :multiple true
                              :type     :string
                              :as       "Policy that you want beagle to evaluate and check if it is breaking any rule."}
                             {:option   "laws" :short "l"
                              :type     :string
                              :as       "Path containing all the laws that a policy can not break."}]
               :runs        engine/cli}]})

(defn -main [& args]
  (climatic/run-cmd args CONFIGURATION))
