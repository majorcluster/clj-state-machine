(ns clj-state-machine.routes.params-test
  (:require [clojure.test :refer :all]
            [clj-state-machine.routes.params :refer :all]))

(deftest validate-mandatory-test
  (testing "when all mandatory are present returns true"
    (is (validate-mandatory {} {:name "Lenin" :age 45} ["name" "age"]))
    (is (validate-mandatory {} {:name "Lenin" :age 45} []))
    (is (validate-mandatory {} {:name "Lenin" :age 45} ["name"])))
  (testing "when mandatory are not present throws ex-info"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                      #"Mandatory fields validation failed"
                      (validate-mandatory {} {:name "Lenin"} ["name" "age"])))
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Mandatory fields validation failed"
                          (validate-mandatory {} {} ["name" "age"])))
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Mandatory fields validation failed"
                          (validate-mandatory {} {:name "Lenin"} ["age"])))
    ))

(deftest mop-fields-test
  (testing "when all allowed fields are present returns body"
    (is (= {:name "Lenin"}
           (mop-fields {:name "Lenin"} ["name"])))
    (is (= {:name "Lenin" :age 47}
           (mop-fields {:name "Lenin" :age 47} ["name" "age"])))
    (is (= {}
           (mop-fields {} []))))
  (testing "when not allowed fields are present they are removed"
    (is (= {:name "Lenin"}
           (mop-fields {:name "Lenin" :hacking "trying"} ["name"])))
    (is (= {:name "Lenin" :age 47}
           (mop-fields {:name "Lenin" :age 47 :hacking "trying"} ["name" "age"])))
    (is (= {}
           (mop-fields {:hacking "trying"} []))))
  )
