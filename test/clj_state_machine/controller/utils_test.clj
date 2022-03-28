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