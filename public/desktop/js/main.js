




/* ============================================================
 * bootstrap-buttons.js v1.4.0
 * http://twitter.github.com/bootstrap/javascript.html#buttons
 * ============================================================
 * Copyright 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ */

!function( $ ){

  "use strict"

  function setState(el, state) {
    var d = 'disabled'
      , $el = $(el)
      , data = $el.data()

    state = state + 'Text'
    data.resetText || $el.data('resetText', $el.html())

    $el.html( data[state] || $.fn.button.defaults[state] )

    state == 'loadingText' ?
      $el.addClass(d).attr(d, d) :
      $el.removeClass(d).removeAttr(d)
  }

  function toggle(el) {
    $(el).toggleClass('active')
  }

  $.fn.button = function(options) {
    return this.each(function () {
      if (options == 'toggle') {
        return toggle(this)
      }
      options && setState(this, options)
    })
  }

  $.fn.button.defaults = {
    loadingText: 'loading...'
  }

  $(function () {
    $('body').delegate('.btn[data-toggle]', 'click', function () {
      $(this).button('toggle')
    })
  })

}( window.jQuery || window.ender );
/* ========================================================
 * bootstrap-tabs.js v1.4.0
 * http://twitter.github.com/bootstrap/javascript.html#tabs
 * ========================================================
 * Copyright 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ======================================================== */


!function( $ ){

  "use strict"

  function activate ( element, container ) {
    container
      .find('> .active')
      .removeClass('active')
      .find('> .dropdown-menu > .active')
      .removeClass('active')

    element.addClass('active')

    if ( element.parent('.dropdown-menu') ) {
      element.closest('li.dropdown').addClass('active')
    }
  }

  function tab( e ) {
    var $this = $(this)
      , $ul = $this.closest('ul:not(.dropdown-menu)')
      , href = $this.attr('href')
      , previous
      , $href

    if ( /^#\w+/.test(href) ) {
      e.preventDefault()

      if ( $this.parent('li').hasClass('active') ) {
        return
      }

      previous = $ul.find('.active a').last()[0]
      $href = $(href)

      activate($this.parent('li'), $ul)
      activate($href, $href.parent())

      $this.trigger({
        type: 'change'
      , relatedTarget: previous
      })
    }
  }


 /* TABS/PILLS PLUGIN DEFINITION
  * ============================ */

  $.fn.tabs = $.fn.pills = function ( selector ) {
    return this.each(function () {
      $(this).delegate(selector || '.tabs li > a, .pills > li > a', 'click', tab)
    })
  }

  $(document).ready(function () {
    $('body').tabs('ul[data-tabs] li > a, ul[data-pills] > li > a')
  })

}( window.jQuery || window.ender );

/* ==========================================================
 * bootstrap-alerts.js v1.4.0
 * http://twitter.github.com/bootstrap/javascript.html#alerts
 * ==========================================================
 * Copyright 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== */


!function( $ ){

  "use strict"

  /* CSS TRANSITION SUPPORT (https://gist.github.com/373874)
   * ======================================================= */

   var transitionEnd

   $(document).ready(function () {

     $.support.transition = (function () {
       var thisBody = document.body || document.documentElement
         , thisStyle = thisBody.style
         , support = thisStyle.transition !== undefined || thisStyle.WebkitTransition !== undefined || thisStyle.MozTransition !== undefined || thisStyle.MsTransition !== undefined || thisStyle.OTransition !== undefined
       return support
     })()

     // set CSS transition event type
     if ( $.support.transition ) {
       transitionEnd = "TransitionEnd"
       if ( $.browser.webkit ) {
        transitionEnd = "webkitTransitionEnd"
       } else if ( $.browser.mozilla ) {
        transitionEnd = "transitionend"
       } else if ( $.browser.opera ) {
        transitionEnd = "oTransitionEnd"
       }
     }

   })

 /* ALERT CLASS DEFINITION
  * ====================== */

  var Alert = function ( content, options ) {
    this.settings = $.extend({}, $.fn.alert.defaults, options)
    this.$element = $(content)
      .delegate(this.settings.selector, 'click', this.close)
  }

  Alert.prototype = {

    close: function (e) {
      var $element = $(this).parent('.alert-message')

      e && e.preventDefault()
      $element.removeClass('in')

      function removeElement () {
        $element.remove()
      }

      $.support.transition && $element.hasClass('fade') ?
        $element.bind(transitionEnd, removeElement) :
        removeElement()
    }

  }


 /* ALERT PLUGIN DEFINITION
  * ======================= */

  $.fn.alert = function ( options ) {

    if ( options === true ) {
      return this.data('alert')
    }

    return this.each(function () {
      var $this = $(this)

      if ( typeof options == 'string' ) {
        return $this.data('alert')[options]()
      }

      $(this).data('alert', new Alert( this, options ))

    })
  }

  $.fn.alert.defaults = {
    selector: '.close'
  }

  $(document).ready(function () {
    new Alert($('body'), {
      selector: '.alert-message[data-alert] .close'
    })
  })

}( window.jQuery || window.ender );