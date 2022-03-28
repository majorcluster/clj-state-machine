(ns clj-state-machine.controller.utils
  (:use clojure.pprint))


(def headers
  {"Content-Type" "application/json"})

(def common-messages
  {:bad-format {:status 400 :headers headers :body {:message ""}}
   :not-found {:status 400 :headers headers :body {:message ""}}
   :none {:status 500 :headers headers :body {:message ""}}})

(defn common-with-custom-message
  [ks message]
  (let [complete-message (ks common-messages)
        body (:body complete-message)
        new-body (assoc body :message message)]
    (assoc complete-message :body new-body)))

(defn format-message
  [message & params]
  (if (and message (.contains message "%s"))
    (apply format message params)
    message))

(defn common-with-custom-message-n-params
  [ks message & params]
  (if (and message (.contains message "%s"))
    (let [message-formatted (apply format message params)
          message-definition (ks common-messages)
          body (:body message-definition)
          new-body (assoc body :message message-formatted)]
      (assoc message-definition :body new-body))
    (common-with-custom-message ks message)))