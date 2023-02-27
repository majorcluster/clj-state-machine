(ns clj-state-machine.ports.http-in.routes.workflow
  (:require [clj-data-adapter.core :as data-adapter]
            [clj-state-machine.adapters.commons :as adapters.commons]
            [clj-state-machine.configs :as configs]
            [clj-state-machine.controllers.utils :as controllers.utils]
            [clj-state-machine.controllers.workflow :as controllers.workflow]
            [clj-state-machine.ports.http-in.routes.utils :as routes.utils]
            [clj-state-machine.wire.http-in.commons :as in.commons]
            [clj-state-machine.wire.http-in.workflow :as in.workflow]
            [pedestal-api-helper.params-helper :as p-helper]
            [schema.core :as s])
  (:import (clojure.lang ExceptionInfo)))

(s/defn get-workflow :- (in.commons/Response in.workflow/GetWorkflowPayloadDef)
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :workflow-id)]
    (cond (or (p-helper/is-uuid id)
              (nil? id)) (if-let [found (controllers.workflow/get-facade (adapters.commons/str->uuid id))]
                           {:status  200
                            :headers controllers.utils/headers
                            :body    {:message ""
                                      :payload (data-adapter/transform-keys data-adapter/namespaced-key->kebab-key found)}}
                           (controllers.utils/not-found-message language "workflow" "id"))
          :else (controllers.utils/not-found-message language "workflow" "id"))))

(s/defn post-workflow :- (in.commons/Response in.workflow/PostPutWorkflowPayloadDef)
  [request :- {s/Keyword s/Any}]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["name"]
          allowed-fields ["name"]
          language (configs/get-language request)
          field-msg (controllers.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      {:status  200
       :headers controllers.utils/headers
       :body    {:message ""
                 :payload {:id (-> (partial data-adapter/kebab-key->namespaced-key "workflow")
                                   (data-adapter/transform-keys body)
                                   controllers.workflow/upsert-facade)}}})
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(s/defn patch-workflow :- (in.commons/Response in.workflow/PostPutWorkflowPayloadDef)
  [request :- {s/Keyword s/Any}]
  (try
    (let [crude-body (:json-params request)
          mandatory-fields ["id","name"]
          allowed-fields ["id","name"]
          language (configs/get-language request)
          field-msg (controllers.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      {:status  200
       :headers controllers.utils/headers
       :body    {:message ""
                 :payload {:id (-> (partial data-adapter/kebab-key->namespaced-key "workflow")
                                   (data-adapter/transform-keys body)
                                   controllers.workflow/upsert-facade)}}})
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(s/defn delete-workflow :- {s/Keyword s/Any}
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :workflow-id)]
    (when (p-helper/is-uuid id) (controllers.workflow/delete-facade language (adapters.commons/str->uuid id)))
    {:status 204}))
