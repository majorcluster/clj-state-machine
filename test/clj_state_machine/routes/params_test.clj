(ns clj-state-machine.routes.params-test
  (:require [clojure.test :refer :all]
            [clj-state-machine.routes.params :refer :all]
            [midje.sweet :refer :all]
            [matcher-combinators.test]
            [clj-state-machine.model.utils :as m.utils])
  (:import (clojure.lang ExceptionInfo)))

(deftest validate-mandatory-test
  (testing "when all mandatory are present returns true"
    (is (validate-mandatory {} {:name "Lenin" :age 45} ["name" "age"]))
    (is (validate-mandatory {} {:name "Lenin" :age 45} []))
    (is (validate-mandatory {} {:name "Lenin" :age 45} ["name"])))
  (testing "when mandatory are not present throws ex-info"
    (is (thrown-match? ExceptionInfo
                      {:type :bad-format
                       :message "Field :age is not present. "}
                      (validate-mandatory {} {:name "Lenin"} ["name" "age"])))
    (is (thrown-match? ExceptionInfo
                      {:type :bad-format
                       :message "Field :name is not present. Field :age is not present. "}
                      (validate-mandatory {} {} ["name" "age"])))
    (is (thrown-match? ExceptionInfo
                      {:type :bad-format
                       :message "Field :age is not present. "}
                      (validate-mandatory {} {:name "Lenin"} ["age"])))
    ))

(deftest extract-field-value-test
  (testing "when being not uuid return right type"
    (is (= "17"
          (extract-field-value :age {:age "17"})))
    (is (= 34
           (extract-field-value :age {:age 34})))
    )
  (testing "when being uuid it gets converted"
    (let [new-uuid (m.utils/uuid)
          uuid-as-string (m.utils/uuid-as-string new-uuid)]
      (is (= new-uuid
             (extract-field-value :id {:id uuid-as-string}))))))

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
  (let [new-uuid (m.utils/uuid)
        uuid-as-string (m.utils/uuid-as-string new-uuid)]
   (testing "when having uuid it is converted"
    (= {:name "Lenin" :id new-uuid}
       (mop-fields {:name "Lenin", :id uuid-as-string} ["name" "id"]))))
  )
