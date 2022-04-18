(ns clj-state-machine.routes.workflow
  (:require [clj-state-machine.routes.params :as r.params]
            [clj-state-machine.routes.utils :as r.utils]
            [clj-state-machine.controller.workflow :as c.workflow]
            [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.config :as config])
  (:use clojure.pprint)
  (:import (clojure.lang ExceptionInfo)))

(defn get-workflow
  [request]
  (let [params (get request :path-params)
        language (config/get-language request)
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
          body (r.params/validate-and-mop request crude-body mandatory-fields allowed-fields)]
      (c.workflow/upsert-facade body))
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn patch-workflow
  [request]
  (try
    (let [crude-body (:json-params request)
          ;remove name if having another attr
          mandatory-fields ["id","name"]
          allowed-fields ["id","name"]
          body (r.params/validate-and-mop request crude-body mandatory-fields allowed-fields)]
      (c.workflow/upsert-facade body)
      {:status 204})
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn delete-workflow
  [request]
  (let [params (get request :path-params)
        language (config/get-language request)
        id (get params :workflow-id)]
    (c.workflow/delete-facade language id)))
