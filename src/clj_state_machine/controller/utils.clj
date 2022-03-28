(ns clj-state-machine.controller.utils
  (:require [clojure.set :as cset]
            [clojure.string :as cstring]
            [clj-state-machine.i18n.translations :as i.t])
  (:use     clojure.pprint))


;default headers
(def headers
  {"Content-Type" "application/json"})

(def common-messages
  {:bad-format {:status 400 :headers headers :body {:message ""}}
   :not-found {:status 400 :headers headers :body {:message ""}}
   :none {:status 500 :headers headers :body {:message ""}}})

(def uuid-pattern
  #"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

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
  (let [route-keys (keys entity)
        to-reduce-keys (concat [{}] route-keys)
        renamed-keys (reduce (fn [map key]
                               (let [key-string (str key)
                                     entity-str-search (str ":" entity-name "/")
                                     new-key-name (cstring/replace key-string (re-pattern entity-str-search) "")
                                     new-key-name (cstring/replace new-key-name (re-pattern ":") "")]
                                 (assoc map key (keyword new-key-name))))
                             to-reduce-keys)]
    (cset/rename-keys entity renamed-keys)))

(defn not-found-message
  [language entity-name search-field-name]
  (let [translations (language i.t/all)
        crude-translation (:not-found translations)]
    (common-with-custom-message-n-params :not-found crude-translation entity-name search-field-name)))

(defn is-uuid
  [id]
  (re-matches uuid-pattern id))