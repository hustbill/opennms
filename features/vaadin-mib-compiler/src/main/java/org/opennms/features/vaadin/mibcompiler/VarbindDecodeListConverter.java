/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.features.vaadin.mibcompiler;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.opennms.features.vaadin.mibcompiler.model.DecodeDTO;
import org.opennms.features.vaadin.mibcompiler.model.DecodeDTOList;
import org.vaadin.addon.customfield.PropertyConverter;

/**
 * The Class VarbindDecodeListConverter.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a> 
 */
@SuppressWarnings("serial")
public class VarbindDecodeListConverter extends PropertyConverter<ArrayList<DecodeDTO>, String> {

    /**
     * Instantiates a new VarbindDecode list converter.
     */
    public VarbindDecodeListConverter() {
        super(DecodeDTOList.class);
    }

    /* (non-Javadoc)
     * @see org.vaadin.addon.customfield.PropertyConverter#format(java.lang.Object)
     */
    @Override
    public String format(ArrayList<DecodeDTO> propertyValue) {
        return StringUtils.join(propertyValue, "\n");
    }

    /* (non-Javadoc)
     * @see org.vaadin.addon.customfield.PropertyConverter#parse(java.lang.Object)
     */
    @Override
    public ArrayList<DecodeDTO> parse(String fieldValue) {
        ArrayList<DecodeDTO> list = new ArrayList<DecodeDTO>();
        for (String s : fieldValue.split("\n")) {
            String[] parts = s.split("=");
            DecodeDTO d = new DecodeDTO();
            d.setVarbindvalue(parts[0].trim());
            d.setVarbinddecodedstring(parts[1].trim());
            list.add(d);
        }
        return list;
    }

}
