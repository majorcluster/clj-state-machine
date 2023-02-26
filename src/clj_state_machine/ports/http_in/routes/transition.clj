(ns clj-state-machine.ports.http-in.routes.transition
  (:require [clj-data-adapter.core :as data-adapter]
            [clj-state-machine.adapters.commons :as adapters.commons]
            [clj-state-machine.configs :as configs]
            [clj-state-machine.controllers.transition :as controllers.transition]
            [clj-state-machine.controllers.utils :as controllers.utils]
            [clj-state-machine.ports.http-in.routes.utils :as routes.utils]
            [clj-state-machine.wire.http-in.commons :as in.commons]
            [clj-state-machine.wire.http-in.transition :as in.transition]
            [pedestal-api-helper.params-helper :as p-helper]
            [schema.core :as s])
  (:import (clojure.lang ExceptionInfo)))

(s/defn get-transition :- (in.commons/Response in.transition/GetTransitionPayloadDef)
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (-> params :transition-id)]
    (cond (or (p-helper/is-uuid id)
              (nil? id)) (if-let [found (controllers.transition/get-facade (adapters.commons/str->uuid id))]
                                     {:status  200
                                      :headers controllers.utils/headers
                                      :body    {:message ""
                                                :payload (data-adapter/transform-keys data-adapter/namespaced-key->kebab-key found)}}
                                     (controllers.utils/not-found-message language "transition" "id"))
          :else (controllers.utils/not-found-message language "transition" "id"))))

(defn extract-workflow-id!!
  [request]
  (let [workflow-id (-> request
                        :path-params
                        :workflow-id)]
    (cond workflow-id workflow-id
          :else ((throw (ex-info "Mandatory fields validation failed" {:type :bad-format
                                                                       :validation-messages ["Path param :workflow-id not present"]}))))))

(defn post-transition
  [request]
  (try
    (let [crude-body (:json-params request)
          workflow-id (extract-workflow-id!! request)
          mandatory-fields ["name"]
          allowed-fields ["name"]
          language (configs/get-language request)
          field-msg (controllers.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (controllers.transition/upsert-facade workflow-id body))
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(defn patch-transition
  [request]
  (try
    (let [crude-body (:json-params request)
          workflow-id (extract-workflow-id!! request)
          mandatory-fields ["id","name"]
          allowed-fields ["id","name"]
          language (configs/get-language request)
          field-msg (controllers.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (controllers.transition/upsert-facade workflow-id body)
      {:status 204})
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(defn delete-transition
  [request]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :transition-id)]
    (controllers.transition/delete-facade language id)))
