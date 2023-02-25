(ns clj-state-machine.controllers.utils
  (:require [clj-state-machine.i18n.translations :as i.t]
            [clojure.set :as cset]
            [clojure.string :as cstring]))

;default headers
(def headers
  {"Content-Type" "application/json"})

(def common-messages
  {:bad-format {:status 400 :headers headers :body {:message ""}}
   :not-found {:status 404 :headers headers :body {:message ""}}
   :none {:status 500 :headers headers :body {:message ""}}})

(defn common-with-custom-message
  [ks message]
  (let [complete-message (ks common-messages)
        body (:body complete-message)
        new-body (assoc body :message message)]
    (assoc complete-message :body new-body)))

(defn common-with-validation-messages
  [ks validation-messages]
  (let [complete-message (ks common-messages)
        body (:body complete-message)
        new-body (assoc body :validation-messages validation-messages)]
    (assoc complete-message :body new-body)))

(defn format-message
  ([message]
   message)
  ([message & params]
   (if (and message (.contains message "%s"))
     (apply format message params)
     message)))

(defn common-with-custom-message-n-params
  [ks message & params]
  (if (and message (.contains message "%s"))
    (let [message-formatted (apply format message params)
          message-definition (ks common-messages)
          body (:body message-definition)
          new-body (assoc body :message message-formatted)]
      (assoc message-definition :body new-body))
    (common-with-custom-message ks message)))

(defn redefine-entity-keys
  [entity-name entity]
  (let [route-keys (keys entity)
        to-reduce-keys (concat [{}] route-keys)
        renamed-keys (reduce (fn [map key]
                               (let [key-string (name key)
                                     new-key-name (str entity-name "/" key-string)]
                                 (assoc map key (keyword new-key-name))))
                             to-reduce-keys)]
    (cset/rename-keys entity renamed-keys)))

(defn undefine-entity-keys
  [entity-name entity]
  (cond (map? entity) (let [route-keys (keys entity)
                            to-reduce-keys (concat [{}] route-keys)
                            renamed-keys (reduce (fn [map key]
                                                   (let [key-string (str key)
                                                         entity-str-search (str ":" entity-name "/")
                                                         new-key-name (cstring/replace key-string (re-pattern entity-str-search) "")
                                                         new-key-name (cstring/replace new-key-name (re-pattern ":") "")]
                                                     (assoc map key (keyword new-key-name))))
                                                 to-reduce-keys)]
                        (cset/rename-keys entity renamed-keys))
        (vector? entity) (reduce (fn [col entity]
                                   (let [undefined (undefine-entity-keys entity-name entity)]
                                     (conj col undefined)))
                                 []
                                 entity)
        :else entity))

(defn not-found-message
  [language entity-name search-field-name]
  (let [translations (language i.t/all)
        crude-translation (:not-found translations)]
    (common-with-custom-message-n-params :not-found crude-translation entity-name search-field-name)))

(defn get-message
  [language message_key & params]
  (let [translations (language i.t/all)
        crude-translation (message_key translations)]
    (apply format-message crude-translation params)))
