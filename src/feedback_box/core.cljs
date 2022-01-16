(ns feedback-box.core
  (:require [ajax.core :refer [POST]]
            [rum.core :as rum]))

(defn send-data [endpoint]
  (let [*aft* (.getAttribute (. js/document (getElementById "aft")) "data-aft")
        in    (.-value (. js/document (getElementById "feedback-input")))]
    (POST endpoint
          {:params  {:feedback (str in)}
           :headers {"x-xsrf-token" *aft*}
           :format  :text})
    (.log js/console "aft: " *aft*)
    (.log js/console "input: " in)))

(rum/defcs feedback-box < (rum/local false :showing?)
  [state]
  (let [showing?             (:showing? state)
        dartar               (. js/document (getElementById "dartar"))
        endpoint             (.getAttribute dartar "data-endpoint")
        greeting             (.getAttribute dartar "data-greeting")
        feedback-placeholder (.getAttribute dartar "data-feedback-placeholder")
        email-placeholder    (.getAttribute dartar "data-email-placeholder")]
    (.log js/console endpoint greeting feedback-placeholder email-placeholder)
    [:<>
     [:div {:class "toggle-button"
            :on-click
            (fn [_] (swap! showing? not))}
      "📣"]
     (when @showing?
       [:<>
        [:form.form-container#feedback-form
         {:on-submit #(do (.preventDefault %)
                          (reset! showing? false)
                          (send-data endpoint))}
         (when-not endpoint
           (let [warning-msg "Warning: No endpoint set!"]
             (js/console.log warning-msg)
             [:span#warning warning-msg]))
         [:span.greeting (or greeting
                             "Please send us feedback.")]
         [:textarea
          {:id          "feedback-input"
           :form        "feedback-form"
           :rows        5
           :placeholder (or feedback-placeholder
                            "Type feedback here.")
           :required    true}]
         [:span.form-field
          [:label {:for :email} "Email:"]
          [:input {:id          "email-input"
                   :form        "feedback-form"
                   :type        :email
                   :placeholder (or email-placeholder
                                    "user@example.com")
                   :required    true}]]
         [:input {:type     :submit
                  :required true}]]])]))

(defn start []
  ;; start is called by init and after code reloading finishes
  ;; this is controlled by the :after-load in the config
  (rum/mount (feedback-box)
             (. js/document (getElementById "fbbi"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
