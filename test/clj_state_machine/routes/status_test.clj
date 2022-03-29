(ns clj-state-machine.routes.status-test
  (:require [clojure.test :refer :all]
            [integration_core :refer :all]
            [io.pedestal.test :as p.test]
            [clj-state-machine.db.status :as db.status]
            [clj-state-machine.db.entity :as db.entity]
            [clj-state-machine.db.config :as db.config]
            [clj-state-machine.model.utils :as m.utils]
            [clj-state-machine.model.utils :as m.utils]
            [clojure.data.json :as json]
            [clojure.test.check.generators :as gen])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(setup)

(def status-present-datomic
  {:status/id (m.utils/uuid)
   :status/name "on-progress"})

(def base-message
  {:message ""
   :payload {}})

(def json-header
  {"Content-Type" "application/json"})

(defn- map-as-json
  [map]
  (json/write-str map))

(defn- json-as-map
  [json-str]
  (json/read-str json-str :key-fn keyword))

(defn- status-present-view
  []
  {:id (get status-present-datomic :status/id)
   :name (get status-present-datomic :status/name)})

(defn- id-as-string
  [entity id-ks]
  (-> entity
      id-ks
      (m.utils/uuid-as-string)))

(defn- insert-test-data []
  (db.status/upsert! status-present-datomic))

(insert-test-data)

(deftest get-status
  (testing "Already present status is gotten"
    (let [expected-resp (assoc base-message :payload (status-present-view))
          status-present-get-url (str "/status/"
                                      (id-as-string
                                        status-present-datomic
                                        :status/id))
          actual-resp (p.test/response-for (:core @test-server) :get status-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp)))))

  (testing "non uuid gives not found"
    (let [expected-resp {:message "status was not found with given id"}
          status-present-get-url "/status/39384"
          actual-resp (p.test/response-for (:core @test-server) :get status-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "not present uuid gives not found"
    (let [expected-resp {:message "status was not found with given id"}
          status-present-get-url (str "/status/" (m.utils/uuid))
          actual-resp (p.test/response-for (:core @test-server) :get status-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp))))))

(deftest post-status
  (testing "insert with mandatory params works"
    (let [new-status (dissoc (status-present-view) :id)
          new-name "dispatching"
          new-status (assoc new-status :name new-name)
          actual-resp (p.test/response-for (:core @test-server)
                                           :post "/status"
                                           :headers json-header
                                           :body (map-as-json new-status))
          body-as-map (json-as-map (:body actual-resp))
          id-gotten (get-in body-as-map [:payload :id] 0)
          id-gotten-as-uuid (UUID/fromString id-gotten)
          conn (db.config/connect!)
          status-in-db (db.entity/find-by-id conn :status/id id-gotten-as-uuid)]
      (is (not (= 0 id-gotten)))
      (is (= 200 (:status actual-resp)))
      (is (= (:status/id status-in-db)
             id-gotten-as-uuid))))
  (testing "insert with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :post "/status"
                                           :headers json-header
                                           :body "")
          expected-resp {:message "Field :name is not present. "}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp))))))