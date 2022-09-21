(ns beagle.laws-test
  (:require
   [beagle.laws :as laws]
   [clojure.test :refer :all]))

(deftest ->simulate-custom-policy
  (testing "when valid laws are provided."
    (let [expected-output {:ActionNames ["iam:*"], :ResourceArns ["*"]}
          laws [{:Statement [{:Action ["iam:*"] :Resource ["*"]}]}]]
      (is (= expected-output (laws/->simulate-custom-policy laws)))))

  (testing "when not valid laws are provided."
    (let [expected-output {:ActionNames [] , :ResourceArns []}
          laws [{:Statement [{:Actions ["iam:*"] :Resources ["*"]}]}]]
      (is (= expected-output (laws/->simulate-custom-policy laws))))))
