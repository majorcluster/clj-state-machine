(ns clj-state-machine.routes.status
  (:require [clj-state-machine.routes.utils :as r.utils]
            [clj-state-machine.controller.status :as c.status]
            [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.config :as config]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (clojure.lang ExceptionInfo)))

(defn get-status
  [request]
  (let [params (get request :path-params)
        language (config/get-language request)
        id (get params :status-id)
        found (c.status/get-facade id)]
    (cond found {:status 200
                 :headers c.utils/headers
                 :body {:message ""
                        :payload found}}
          :else (c.utils/not-found-message language "status" "id") )))

(defn post-status
  [request]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["name"]
          allowed-fields ["name"]
          language (config/get-language request)
          field-msg (c.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (c.status/upsert-facade body))
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn patch-status
  [request]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["id","name"]
          allowed-fields ["id","name"]
          language (config/get-language request)
          field-msg (c.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (c.status/upsert-facade body)
      {:status 204})
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn delete-status
  [request]
  (let [params (get request :path-params)
        language (config/get-language request)
        id (get params :status-id)]
    (c.status/delete-facade language id)))