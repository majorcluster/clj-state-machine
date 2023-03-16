(ns clj-state-machine.ports.http-in.routes.status
  (:require [clj-data-adapter.core :as data-adapter]
            [clj-state-machine.adapters.commons :as adapters.commons]
            [clj-state-machine.configs :as configs]
            [clj-state-machine.controllers.status :as controllers.status]
            [clj-state-machine.controllers.utils :as controllers.utils]
            [clj-state-machine.ports.http-in.routes.utils :as routes.utils]
            [clj-state-machine.wire.http-in.commons :as in.commons]
            [clj-state-machine.wire.http-in.status :as in.status]
            [pedestal-api-helper.params-helper :as p-helper]
            [schema.core :as s])
  (:import (clojure.lang ExceptionInfo)))

(s/defn get-status :- (in.commons/Response in.status/GetStatusPayloadDef)
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (-> params :status-id)]
    (cond (or (p-helper/is-uuid id)
              (nil? id)) (if-let [found (controllers.status/get-facade (adapters.commons/str->uuid id))]
                           {:status  200
                            :headers controllers.utils/headers
                            :body    {:message ""
                                      :payload (->> found
                                                    (data-adapter/transform-keys data-adapter/namespaced-key->kebab-key)
                                                    (data-adapter/transform-values data-adapter/uuid->str))}}
                           (controllers.utils/not-found-message language "status" "id"))
          :else (controllers.utils/not-found-message language "status" "id"))))

(s/defn post-status :- (in.commons/Response in.status/PostPutStatusPayloadDef)
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
                 :payload {:id (-> (partial data-adapter/kebab-key->namespaced-key "status")
                                   (data-adapter/transform-keys body)
                                   controllers.status/upsert-facade)}}})
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(s/defn patch-status :- (in.commons/Response in.status/PostPutStatusPayloadDef)
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
                 :payload {:id (-> (partial data-adapter/kebab-key->namespaced-key "status")
                                   (data-adapter/transform-keys body)
                                   controllers.status/upsert-facade)}}})
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(s/defn delete-status :- {s/Keyword s/Any}
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :status-id)]
    (when (p-helper/is-uuid id) (controllers.status/delete-facade language (adapters.commons/str->uuid id)))
    {:status 204}))
