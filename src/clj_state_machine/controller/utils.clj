(ns clj-state-machine.controller.utils
  (:use clojure.pprint))


(def common-messages
  {:bad-format {:status 400 :message ""}
   :not-found {:status 400 :message ""}})

(defn common-with-custom-message
  [ks message]
  (-> common-messages
      ks
      (assoc :message message)))

(defn format-message
  [message & params]
  (if (and message (.contains message "%s"))
    (apply format message params)
    message))

(defn common-with-custom-message-n-params
  [ks message & params]
  (if (and message (.contains message "%s"))
    (let [message-formatted (apply format message params)]
      (-> common-messages
          ks
          (assoc :message message-formatted)))
    (common-with-custom-message ks message)))