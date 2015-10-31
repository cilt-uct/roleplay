/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/uct/useralias/trunk/tool/src/java/org/sakaiproject/useralias/tool/params/CSVViewParamaters.java $
 * $Id: CSVViewParamaters.java 75805 2011-07-27 10:14:38Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.useralias.tool.params;

import uk.org.ponder.rsf.viewstate.SimpleViewParameters;

public class XLSXViewParamaters extends SimpleViewParameters {
	
	public String realm;
	
	public XLSXViewParamaters() {
		
	}
	
	public XLSXViewParamaters(String view) {
		this.viewID = view; 
	}
	public XLSXViewParamaters(String view, String realm) {
		this.viewID = view;
		this.realm = realm;
	}
}
