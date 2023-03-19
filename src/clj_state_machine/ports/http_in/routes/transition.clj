(ns clj-state-machine.ports.http-in.routes.transition
  (:require [clj-state-machine.adapters.commons :as adapters.commons]
            [clj-state-machine.adapters.transition :as adapters.transition]
            [clj-state-machine.configs :as configs]
            [clj-state-machine.controllers.transition :as controllers.transition]
            [clj-state-machine.controllers.utils :as controllers.utils]
            [clj-state-machine.ports.http-in.routes.utils :as routes.utils]
            [clj-state-machine.wire.http-in.commons :as in.commons]
            [clj-state-machine.wire.http-in.transition :as in.transition]
            [pedestal-api-helper.params-helper :as p-helper]
            [schema.core :as s])
  (:import (clojure.lang ExceptionInfo)))

(defn- all-uuid-or-nil
  [& ids]
  (every? #(or (not %)
               (p-helper/is-uuid %)) ids))

(s/defn get-transition :- (in.commons/Response in.transition/GetTransitionPayloadDef)
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (-> params :transition-id)
        workflow-id (-> params :workflow-id)]
    (cond (all-uuid-or-nil id workflow-id) (if-let [found (controllers.transition/get-facade (adapters.commons/str->uuid id)
                                                                                             (adapters.commons/str->uuid workflow-id))]
                                             {:status  200
                                              :headers controllers.utils/headers
                                              :body    {:message ""
                                                        :payload (adapters.transition/internal->wire found)}}
                                             (controllers.utils/not-found-message language "transition" "id"))
          :else (controllers.utils/not-found-message language "transition" "id"))))

(s/defn get-transition-by-status-from :- (in.commons/Response in.transition/GetTransitionPayloadDef)
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        workflow-id (-> params :workflow-id)
        status-from (-> params :status-from)]
    (cond (all-uuid-or-nil status-from
                           workflow-id) (if-let [found (controllers.transition/get-by-status-from-facade (adapters.commons/str->uuid workflow-id)
                                                                                                         (adapters.commons/str->uuid status-from))]
                                          {:status  200
                                           :headers controllers.utils/headers
                                           :body    {:message ""
                                                     :payload (adapters.transition/internal->wire found)}}
                                          (controllers.utils/not-found-message language "transition" "id"))
          :else (controllers.utils/not-found-message language "transition" "id"))))

(s/defn extract-workflow-id!! :- s/Uuid
  [request :- {s/Keyword s/Any}]
  (let [workflow-id (-> request
                        :path-params
                        :workflow-id)]
    (cond (not workflow-id) (throw (ex-info "Mandatory fields validation failed" {:type :bad-format
                                                                                  :message "Path param :workflow-id not present"}))
          (not (p-helper/is-uuid workflow-id)) (throw (ex-info ":worklow-id invalid format" {:type :bad-format
                                                                                             :message "Path param :worklow-id invalid format"}))
          :else (adapters.commons/str->uuid workflow-id))))

(s/defn post-transition :- (in.commons/Response in.transition/PostPutTransitionPayloadDef)
  [request :- {s/Keyword s/Any}]
  (try
    (let [crude-body (:json-params request)
          workflow-id (extract-workflow-id!! request)
          mandatory-fields ["name" "status-to"]
          allowed-fields ["name" "status-to" "status-from"]
          language (configs/get-language request)
          field-msg (controllers.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      {:status  200
       :headers controllers.utils/headers
       :body    {:message ""
                 :payload {:id (-> body
                                   adapters.transition/post-wire->internal
                                   (controllers.transition/upsert-facade workflow-id))}}})
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(s/defn patch-transition :- (in.commons/Response in.transition/PostPutTransitionPayloadDef)
  [request :- {s/Keyword s/Any}]
  (try
    (let [crude-body (:json-params request)
          workflow-id (extract-workflow-id!! request)
          mandatory-fields ["id"]
          allowed-fields ["id","name","status-from","status-to"]
          language (configs/get-language request)
          field-msg (controllers.utils/get-message language :field-not-present)
          body (p-helper/validate-and-mop!! crude-body mandatory-fields allowed-fields field-msg)]
      (cond (<= (count body) 1) (throw (ex-info "Mandatory fields validation failed" {:type :bad-format
                                                                                      :message "At least one of :name,:status-from,:status-to must be present"}))

            :else {:status  200
                   :headers controllers.utils/headers
                   :body    {:message ""
                             :payload {:id (-> body
                                               adapters.transition/patch-wire->internal
                                               (controllers.transition/patch-facade workflow-id))}}}))
    (catch ExceptionInfo e
      (routes.utils/message-catch request e))))

(s/defn delete-transition
  [request :- {s/Keyword s/Any}]
  (let [params (get request :path-params)
        language (configs/get-language request)
        id (get params :transition-id)]
    (when (p-helper/is-uuid id) (controllers.transition/delete-facade language (adapters.commons/str->uuid id)))
    {:status 204}))
