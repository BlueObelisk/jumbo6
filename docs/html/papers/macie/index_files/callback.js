/*
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * Execute callback to fill content box with Entrez Linking information.
 */
function entrez_callback(pmid, callback_url) {
  /*
   * MSIE 5.5 and below have issues with the JavaScript
   * used for Entrez Linking. For now we have to disable
   * the callback until we can track down a proper fix
   * (or everybody sanely upgrades to version 6 or 7!).
   */
  if (navigator) {
    var appname = navigator.appName;
    if (appname == "Microsoft Internet Explorer") {
      var userAgent = navigator["userAgent"];
      var s = "MSIE ";
      var n = -1;      
      if ((n = userAgent.indexOf(s)) != -1) {
        var v = parseFloat(userAgent.substring(n+s.length));
        if (v < 6) {
          return;
        }
      }
    }
  }

  /*
   * Acquire table row element to update, initiate callback
   * to update table with Entrez Links.
   */
  var tr = document.getElementById('entrez_callback_'+pmid);
  if (!tr) {
    return;
  }
  var req = new XMLHttpRequest();
  if (!req) {
    return;
  }
  req.onreadystatechange = function() {
    if (req.readyState == 4 && (req.status == 200 || req.status == 304)) {
      var src = req.responseXML.documentElement;
      var dst = document.createDocumentFragment();
      for (var i = 0; i < src.childNodes.length; i++) {
      	copy_xml_to_html(src.childNodes[i], dst);
      }
      var tbl = tr.parentNode;
      tbl.replaceChild(dst, tr);
    }
  }
  req.open('GET', callback_url, true);
  req.send(null);
}
