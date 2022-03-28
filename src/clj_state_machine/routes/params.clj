(ns clj-state-machine.routes.params
  (:require [schema.core :as s]
            [clj-state-machine.i18n.translations :as i.t]
            [clj-state-machine.config :as config]
            [clj-state-machine.controller.utils :as c.utils])
  (:use clojure.pprint))

(defn validate-mandatory
  [request body fields]
  (let [fields (map #(keyword %) fields)
        not-present (filter (fn [field]
                              (not (contains? body field))) fields)
        language (config/get-language request)
        message-untranslated (:field-not-present (language i.t/all))
        not-present-messages (map (fn [field]
                                    (c.utils/format-message message-untranslated field))
                               not-present)
        not-present-message (apply str not-present-messages)
        ]
    (cond (empty? not-present) true
          :else ((throw (ex-info "Mandatory fields validation failed" {:type :bad-format
                                                                       :message not-present-message}))
                  ))
    ))

(defn mop-fields
  [body fields]
  (let [fields (concat [{}] (map #(keyword %) fields))
        cleaned (reduce (fn [map field]
                          (let [has-field? (contains? body field)
                                value (field body)]
                            (if has-field? (assoc map field value)
                              map)))
                        fields)]
    cleaned))

(s/defn validate-and-mop
  [request
   body
   mandatory :- [s/Str]
   accepted :- [s/Str]]
  (validate-mandatory request body mandatory)
  (mop-fields body accepted))
