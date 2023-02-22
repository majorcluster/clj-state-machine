(ns clj-state-machine.ports.http-in.routes.workflow
  (:require [clj-state-machine.configs :as configs]
            [clj-state-machine.controllers.utils :as c.utils]
            [clj-state-machine.controllers.workflow :as c.workflow]
            [clj-state-machine.ports.http-in.routes.utils :as r.utils]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (clojure.lang ExceptionInfo)))

(defn get-workflow
  [request]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :workflow-id)
        found (c.workflow/get-facade id)]
    (cond found {:status 200
                 :headers c.utils/headers
                 :body {:message ""
                        :payload found}}
          :else (c.utils/not-found-message language "workflow" "id") )))

(defn post-workflow
  [request]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["name"]
          allowed-fields ["name"]
          language (configs/get-language request)
          field-msg (c.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (c.workflow/upsert-facade body))
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn patch-workflow
  [request]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["id","name"]
          allowed-fields ["id","name"]
          language (configs/get-language request)
          field-msg (c.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (c.workflow/upsert-facade body)
      {:status 204})
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn delete-workflow
  [request]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :workflow-id)]
    (c.workflow/delete-facade language id)))
