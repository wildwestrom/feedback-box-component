(ns feedback-box.core
  (:require [ajax.core :refer [POST]]
            [garden.core :refer [css]]
            [garden.selectors :as selector]
            [rum.core :as rum]))

(def styles
  (css
    {:pretty-print? false}
    [:.toggle-button {:cursor           :pointer
                      :background-color "#fff"
                      :font-size        "1.5rem"
                      :width            :min-content
                      :border-width     "2px"
                      :border-radius    "1em"
                      :transition       [:background-color "0.5s"]
                      :user-select      :none}
     [:&:hover {:background-color :gold}]]
    [:#warning {:background    :yellow
                :font-weight   :bold
                :font-size     "1.5rem"
                :border-width  "1px"
                :border-style  :solid
                :border-radius ".375rem"
                :padding       ".25rem"}
     [:&:before {:content       "\"⚠️\""
                 :padding-right ".375rem"}]]
    [:.greeting {:font-size "1.5rem"}
     [:&:before {:content       "\"📣\""
                 :padding-right ".375rem"}]]
    [:.form-container {:display          :flex
                       :background       :white
                       :flex-direction   :column
                       :text-align       :center
                       :max-width        "50ch"
                       :gap              ".5em"
                       :padding          "1em"
                       :width            :fit-content
                       :border           [["1px" :solid :forestgreen]]
                       :border-top-width "4px"
                       :border-radius    ".5em"
                       :box-shadow       [["#888" "5px" "5px" "5px"]]}
     [:textarea {:resize     :vertical
                 :min-height "2rem"}]
     [:span.form-field {:display :flex
                        :gap     ".5rem"}
      [:input {:flex-grow 1}]]]))

(defn send-data [endpoint]
  (let [*aft* (.getAttribute (. js/document (getElementById "aft")) "data-aft")
        in (.-value (. js/document (getElementById "feedback-input")))]
    (POST endpoint
          {:params  {:feedback (str in)}
           :headers {"x-xsrf-token" *aft*}
           :format :text})
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
     [:style styles]
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
