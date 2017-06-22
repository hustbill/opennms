/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest.v2;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.ext.search.SearchBean;
import org.opennms.core.config.api.JaxbListWrapper;
import org.opennms.core.criteria.Alias.JoinType;
import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.core.criteria.restrictions.Restrictions;
import org.opennms.netmgt.dao.api.EventDao;
import org.opennms.netmgt.model.OnmsEvent;
import org.opennms.netmgt.model.OnmsEventCollection;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.web.rest.support.Aliases;
import org.opennms.web.rest.support.CriteriaBehavior;
import org.opennms.web.rest.support.CriteriaBehaviors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Basic Web Service using REST for {@link OnmsEvent} entity.
 *
 * @author <a href="agalue@opennms.org">Alejandro Galue</a>
 */
@Component
@Path("events")
@Transactional
public class EventRestService extends AbstractDaoRestService<OnmsEvent,SearchBean,Integer,Integer> {

    @Autowired
    private EventDao m_dao;

    @Override
    protected EventDao getDao() {
        return m_dao;
    }

    @Override
    protected Class<OnmsEvent> getDaoClass() {
        return OnmsEvent.class;
    }

    @Override
    protected Class<SearchBean> getQueryBeanClass() {
        return SearchBean.class;
    }

    @Override
    protected CriteriaBuilder getCriteriaBuilder(UriInfo uriInfo) {
        final CriteriaBuilder builder = new CriteriaBuilder(getDaoClass(), Aliases.event.toString());

        // 1st level JOINs
        builder.alias("alarm", Aliases.alarm.toString(), JoinType.LEFT_JOIN);
        builder.alias("distPoller", Aliases.distPoller.toString(), JoinType.LEFT_JOIN);
        builder.alias("node", Aliases.node.toString(), JoinType.LEFT_JOIN);
        // TODO: Only add this alias when filtering by category so that we can specify a join condition
        builder.alias("serviceType", Aliases.serviceType.toString(), JoinType.LEFT_JOIN);

        // 2nd level JOINs
        builder.alias(Aliases.node.prop("assetRecord"), Aliases.assetRecord.toString(), JoinType.LEFT_JOIN);
        // Left joins on a toMany relationship need a join condition so that only one row is returned
        builder.alias(Aliases.node.prop("ipInterfaces"), Aliases.ipInterface.toString(), JoinType.LEFT_JOIN, Restrictions.or(Restrictions.eqProperty(Aliases.ipInterface.prop("ipAddress"), Aliases.event.prop("ipAddr")), Restrictions.isNull(Aliases.ipInterface.prop("ipAddress"))));
        builder.alias(Aliases.node.prop("location"), Aliases.location.toString(), JoinType.LEFT_JOIN);
        // Left joins on a toMany relationship need a join condition so that only one row is returned
        builder.alias(Aliases.node.prop("snmpInterfaces"), Aliases.snmpInterface.toString(), JoinType.LEFT_JOIN, Restrictions.or(Restrictions.eqProperty(Aliases.snmpInterface.prop("ifIndex"), Aliases.event.prop("ifIndex")), Restrictions.isNull(Aliases.snmpInterface.prop("ifIndex"))));

        // TODO: Only add this alias when filtering so that we can specify a join condition
        builder.alias(Aliases.node.prop("categories"), Aliases.category.toString(), JoinType.LEFT_JOIN);

        builder.orderBy("eventTime").desc(); // order by event time by default

        return builder;
    }

    @Override
    protected JaxbListWrapper<OnmsEvent> createListWrapper(Collection<OnmsEvent> list) {
        return new OnmsEventCollection(list);
    }

    @Override
    protected Map<String, String> getSearchBeanPropertyMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("event.uei", "event.eventUei");
        return map;
    }

    @Override
    protected Map<String,CriteriaBehavior<?>> getCriteriaBehaviors() {
        final Map<String,CriteriaBehavior<?>> map = new HashMap<>();

        // Root alias
        map.putAll(CriteriaBehaviors.EVENT_BEHAVIORS);

        // 1st level JOINs
        map.putAll(CriteriaBehaviors.ALARM_BEHAVIORS);
        map.putAll(CriteriaBehaviors.DIST_POLLER_BEHAVIORS);
        map.putAll(CriteriaBehaviors.NODE_BEHAVIORS);
        map.putAll(CriteriaBehaviors.SERVICE_TYPE_BEHAVIORS);

        // 2nd level JOINs
        map.putAll(CriteriaBehaviors.ASSET_RECORD_BEHAVIORS);
        map.putAll(CriteriaBehaviors.IP_INTERFACE_BEHAVIORS);
        map.putAll(CriteriaBehaviors.MONITORING_LOCATION_BEHAVIORS);
        map.putAll(CriteriaBehaviors.NODE_CATEGORY_BEHAVIORS);
        map.putAll(CriteriaBehaviors.SNMP_INTERFACE_BEHAVIORS);

        // TODO: distPoller

        return map;
    }

    @Override
    protected OnmsEvent doGet(UriInfo uriInfo, Integer id) {
        return getDao().get(id);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(@Context final SecurityContext securityContext, @Context final UriInfo uriInfo, Event event) {
        if (event.getTime() == null) event.setTime(new Date());
        if (event.getSource() == null) event.setSource("ReST");

        sendEvent(event);
        return Response.noContent().build();
    }

}
