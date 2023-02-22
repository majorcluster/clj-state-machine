(ns clj-state-machine.ports.http-in.routes.transition
  (:require [clj-state-machine.configs :as configs]
            [clj-state-machine.controllers.transition :as c.transition]
            [clj-state-machine.controllers.utils :as c.utils]
            [clj-state-machine.ports.http-in.routes.utils :as r.utils]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (clojure.lang ExceptionInfo)))

(defn get-transition
  [request]
  (let [params (get request :path-params)
        workflow-id (-> request
                        :path-params
                        :workflow-id)
        language (configs/get-language request)
        id (get params :transition-id)
        found (c.transition/get-facade id)]
    (cond found {:status 200
                 :headers c.utils/headers
                 :body {:message ""
                        :payload found}}
          :else (c.utils/not-found-message language "transition" "id"))))

(defn extract-workflow-id!!
  [request]
  (let [workflow-id (-> request
                      :path-params
                      :workflow-id)]
    (cond workflow-id workflow-id
          :else ((throw (ex-info "Mandatory fields validation failed" {:type :bad-format
                                                                       :validation-messages ["Path param :workflow-id not present"]}))
                 ))))

(defn post-transition
  [request]
  (try
    (let [crude-body (:json-params request)
          workflow-id (extract-workflow-id!! request)
          mandatory-fields ["name"]
          allowed-fields ["name"]
          language (configs/get-language request)
          field-msg (c.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (c.transition/upsert-facade workflow-id body))
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn patch-transition
  [request]
  (try
    (let [crude-body (:json-params request)
          workflow-id (extract-workflow-id!! request)
          mandatory-fields ["id","name"]
          allowed-fields ["id","name"]
          language (configs/get-language request)
          field-msg (c.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (c.transition/upsert-facade workflow-id body)
      {:status 204})
    (catch ExceptionInfo e
      (r.utils/message-catch request e))
    ))

(defn delete-transition
  [request]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :transition-id)]
    (c.transition/delete-facade language id)))
