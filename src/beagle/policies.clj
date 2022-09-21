(ns beagle.policies)

#_{:clj-kondo/ignore [:redefined-var]}
(defn read [policy]
  (map slurp policy))
