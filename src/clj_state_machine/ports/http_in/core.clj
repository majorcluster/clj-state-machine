(ns clj-state-machine.ports.http-in.core
  (:require [clj-state-machine.configs :as configs]
            [clj-state-machine.ports.http-in.routes.core :as routes]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(def service {:env (keyword configs/env)
              ::http/routes routes/specs
              ::http/resource-path "/public"
              ::http/router :linear-search

              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})

(defonce runnable-service (http/create-server service))

(defn start
  []
  (http/start runnable-service))

(defn start-dev
  []
  (-> service
      (merge {:env :dev
              ::http/join? false
              ::http/routes #(route/expand-routes (deref #'routes/specs))
              ::http/allowed-origins {:creds true :allowed-origins (constantly true)}
              ::http/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
      http/default-interceptors
      http/dev-interceptors
      http/create-server
      http/start))
