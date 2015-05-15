/*
  Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.

  The MySQL Connector/J is licensed under the terms of the GPLv2
  <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most MySQL Connectors.
  There are special exceptions to the terms and conditions of the GPLv2 as it is applied to
  this software, see the FLOSS License Exception
  <http://www.mysql.com/about/legal/licensing/foss-exception.html>.

  This program is free software; you can redistribute it and/or modify it under the terms
  of the GNU General Public License as published by the Free Software Foundation; version 2
  of the License.

  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with this
  program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
  Floor, Boston, MA 02110-1301  USA

 */

package com.mysql.cj.core.conf;

import java.io.Serializable;
import java.util.Properties;

import javax.naming.RefAddr;
import javax.naming.Reference;

import com.mysql.cj.api.conf.ModifiableProperty;
import com.mysql.cj.api.conf.PropertyDefinition;
import com.mysql.cj.api.conf.RuntimeProperty;
import com.mysql.cj.api.exception.ExceptionInterceptor;

public abstract class ConnectionProperty implements RuntimeProperty, ModifiableProperty, Serializable {

    private static final long serialVersionUID = -3424722534876438236L;

    private PropertyDefinition propertyDefinition;

    protected Object valueAsObject;

    protected int updateCount = 0;

    public ConnectionProperty() {
    }

    protected ConnectionProperty(String propertyNameToSet) {
        this.propertyDefinition = PropertyDefinitions.getPropertyDefinition(propertyNameToSet);
        this.valueAsObject = getPropertyDefinition().getDefaultValue();
    }

    public PropertyDefinition getPropertyDefinition() {
        return this.propertyDefinition;
    }

    public int getUpdateCount() {
        return this.updateCount;
    }

    public void initializeFrom(Properties extractFrom, ExceptionInterceptor exceptionInterceptor) {
        String extractedValue = extractFrom.getProperty(getPropertyDefinition().getName());
        extractFrom.remove(getPropertyDefinition().getName());
        initializeFrom(extractedValue, exceptionInterceptor);
    }

    public void initializeFrom(Reference ref, ExceptionInterceptor exceptionInterceptor) {
        RefAddr refAddr = ref.get(getPropertyDefinition().getName());

        if (refAddr != null) {
            String refContentAsString = (String) refAddr.getContent();

            initializeFrom(refContentAsString, exceptionInterceptor);
        }
    }

    public void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) {
        if (extractedValue != null) {
            setFromString(extractedValue, exceptionInterceptor);
        }
    }

    public Object getValue() {
        return this.valueAsObject;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(Class<T> clazz) {
        return (T) this.valueAsObject;
    }

    public void setValueAsObject(Object value) {
        this.valueAsObject = value;
        this.updateCount++;
    }

    @Override
    public void resetValue() {
        // TODO Auto-generated method stub

    }

}
