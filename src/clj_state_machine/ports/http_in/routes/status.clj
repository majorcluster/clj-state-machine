(ns clj-state-machine.ports.http-in.routes.status
  (:require [clj-state-machine.configs :as configs]
            [clj-state-machine.controllers.status :as c.status]
            [clj-state-machine.controllers.utils :as c.utils]
            [clj-state-machine.ports.http-in.routes.utils :as r.utils]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (clojure.lang ExceptionInfo)))

(defn get-status
  [request]
  (let [params (get request :path-params)
        language (configs/get-language request)
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
          language (configs/get-language request)
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
          language (configs/get-language request)
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
        language (configs/get-language request)
        id (get params :status-id)]
    (c.status/delete-facade language id)))
