(ns clj-state-machine.controller.utils-test
  (:require [clojure.test :refer :all]
            [clj-state-machine.controller.utils :refer :all])
  (:use [clojure.pprint]))

(deftest common-with-custom-message-test
  (testing "existing message will have a custom text"
    (is (= {:status 400 :headers headers :body {:message "custom"}}
           (common-with-custom-message :bad-format "custom")))
    (is (= {:status 400 :headers headers :body {:message ""}}
           (common-with-custom-message :bad-format "")))
    (is (= {:status 400 :headers headers :body {:message nil}}
           (common-with-custom-message :bad-format nil)))))

(deftest common-with-validation-messages-test
  (testing "existing message will have a custom text"
    (is (= {:status 400 :headers headers :body {:message "", :validation-messages [:field "age" :message "Field :age is not present"]}}
           (common-with-validation-messages :bad-format [:field "age" :message "Field :age is not present"])))
    (is (= {:status 400 :headers headers :body {:message "", :validation-messages nil}}
           (common-with-validation-messages :bad-format nil)))
    (is (= {:status 400 :headers headers :body {:message "", :validation-messages []}}
           (common-with-validation-messages :bad-format [])))))

(deftest common-with-custom-messages-params-test
  (testing "existing message will have a text with replaced params"
    (is (= {:status 400 :headers headers :body {:message "custom param"}}
           (common-with-custom-message-n-params :bad-format "custom %s" "param")))
    (is (= {:status 400 :headers headers :body {:message ""}}
           (common-with-custom-message-n-params :bad-format "")))
    (is (= {:status 400 :headers headers :body {:message nil}}
           (common-with-custom-message-n-params :bad-format nil)))
    (is (= {:status 400 :headers headers :body {:message "custom 1 2 3"}}
           (common-with-custom-message-n-params :bad-format "custom %s %s %s" "1" "2" "3")))))

(deftest format-message-test
  (testing "text will be replaced"
    (is (= "my message is nice"
           (format-message "my message is %s" "nice")))
    (is (= "my message is nice"
           (format-message "my message is nice" "bad")))
    (is (= "my message is nice and splendid"
           (format-message "my message is %s and %s" "nice" "splendid")))
    (is (= "my message is nice"
           (format-message "my message is nice")))
    ))

(deftest get-message-test
  (testing "text will be replaced"
    (is (= "Something really odd is going on. Please retry later."
           (get-message :en :fatal)))
    (is (= "Field :name is not present"
           (get-message :en :field-not-present :name)))
    (is (= "Field %s is not present"
           (get-message :en :field-not-present)))
    ))

(deftest redefine-entity-keys-test
  (testing "map has keys redefined"
    (is (= {:status/id 12 :status/name "name"}
           (redefine-entity-keys "status" {:id 12 :name "name"})))
    (is (= {}
           (redefine-entity-keys "status" {})))))

(deftest undefine-entity-keys-test
  (testing "map has keys undefined"
    (is (= {:id 12 :name "name"}
           (undefine-entity-keys "status" {:status/id 12 :status/name "name"})))
    (is (= {:status/id 12 :status/name "name"}
           (undefine-entity-keys "test" {:status/id 12 :status/name "name"})))
    (is (= {}
           (undefine-entity-keys "status" {}))))
  (testing "col has keys undefined"
    (is (= [{:id 12 :name "name"},{:name "second"}]
           (undefine-entity-keys "status" [{:status/id 12 :status/name "name"},{:status/name "second"}])))
    (is (= [{:id 18888 :name "chapolin"}]
           (undefine-entity-keys "status" [{:status/id 18888 :status/name "chapolin"}])))
    (is (= [{:status/id 18888 :status/name "chapolin"}]
           (undefine-entity-keys "test" [{:status/id 18888 :status/name "chapolin"}]))))
  (testing "for other var types return params themselves"
    (is (= [1]
           (undefine-entity-keys "status" [1])))
    (is (= 1
           (undefine-entity-keys "status" 1)))
    (is (= ""
           (undefine-entity-keys "status" "")))))