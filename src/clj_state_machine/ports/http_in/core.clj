(ns clj-state-machine.ports.http-in.core
  (:require [clj-state-machine.configs :as configs]
            [clj-state-machine.ports.http-in.routes.core :as routes]
            [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]))

(def service {:env (keyword configs/env)
              ::http/routes routes/specs
              ::http/resource-path "/public"

              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false
                                        }})

(defonce runnable-service (server/create-server service))

(defn start
  []
  (server/start runnable-service))

(defn start-dev
  []
  (-> service
      (merge {:env :dev
              ::server/join? false
              ::server/routes #(route/expand-routes (deref #'routes/specs))
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
              ::server/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))
