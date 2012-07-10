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

import org.opennms.features.vaadin.mibcompiler.model.DecodeDTO;
import org.opennms.features.vaadin.mibcompiler.model.DecodeDTOList;
import org.vaadin.addon.customfield.FieldWrapper;

import com.vaadin.ui.TextArea;

/**
 * The Class VarbindDecodeListField.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a> 
 */
@SuppressWarnings("serial")
public class VarbindDecodeListField extends FieldWrapper<ArrayList<DecodeDTO>> {
    
    /** The area. */
    private TextArea area;
    
    /**
     * Instantiates a new VarbindDecode list field.
     *
     */
    public VarbindDecodeListField() {
        super(new TextArea(),  new VarbindDecodeListConverter(), DecodeDTOList.class);
        this.area = (TextArea) getWrappedField();
        this.area.setRows(3);
        setCompositionRoot(this.area);
    }
    
    /**
     * Gets the text area.
     *
     * @return the text area
     */
    public TextArea getTextArea() {
        return this.area;
    }

}
