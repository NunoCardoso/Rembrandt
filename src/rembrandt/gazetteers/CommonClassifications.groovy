/** This file is part of REMBRANDT - Named Entity Recognition Software
 *  (http://xldb.di.fc.ul.pt/Rembrandt)
 *  Copyright (c) 2008-2009, Nuno Cardoso, University of Lisboa and Linguateca.
 *
 *  REMBRANDT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  REMBRANDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with REMBRANDT. If not, see <http://www.gnu.org/licenses/>.
 */
package rembrandt.gazetteers

import rembrandt.obj.SemanticClassification as SC
import rembrandt.gazetteers.SemanticClassificationDefinitions as SCD

/**
 * @author Nuno Cardoso
 * Gazetteer of common classifications
 */
class CommonClassifications {

    static final SC person = SC.create(SCD.category.person)
    static final SC person_individual = SC.create(SCD.category.person, SCD.type.individual)
    static final SC person_individualgroup = SC.create(SCD.category.person, SCD.type.individualgroup)
    static final SC person_position = SC.create(SCD.category.person, SCD.type.position)
    static final SC person_positiongroup = SC.create(SCD.category.person, SCD.type.positiongroup)
    static final SC person_member = SC.create(SCD.category.person, SCD.type.member)
    static final SC person_membergroup = SC.create(SCD.category.person, SCD.type.membergroup)
    static final SC person_people = SC.create(SCD.category.person, SCD.type.people)

    static final SC place = SC.create(SCD.category.place)
    static final SC place_human = SC.create(SCD.category.place, SCD.type.human)
    static final SC place_human_street = SC.create(SCD.category.place, SCD.type.human, SCD.subtype.street)
    static final SC place_human_country = SC.create(SCD.category.place, SCD.type.human, SCD.subtype.country)
    static final SC place_human_division = SC.create(SCD.category.place, SCD.type.human, SCD.subtype.division)
    static final SC place_human_humanregion = SC.create(SCD.category.place, SCD.type.human, SCD.subtype.humanregion)
    static final SC place_human_construction = SC.create(SCD.category.place, SCD.type.human, SCD.subtype.construction)
    static final SC place_physical = SC.create(SCD.category.place, SCD.type.physical)
    static final SC place_physical_island = SC.create(SCD.category.place, SCD.type.physical, SCD.subtype.island)
    static final SC place_physical_watercourse = SC.create(SCD.category.place, SCD.type.physical, SCD.subtype.watercourse)
    static final SC place_physical_watermass = SC.create(SCD.category.place, SCD.type.physical, SCD.subtype.watermass)
    static final SC place_physical_mountain = SC.create(SCD.category.place, SCD.type.physical, SCD.subtype.mountain)
    static final SC place_physical_planet = SC.create(SCD.category.place, SCD.type.physical, SCD.subtype.planet)
    static final SC place_physical_physicalregion = SC.create(SCD.category.place, SCD.type.physical, SCD.subtype.physicalregion)
    static final SC place_virtual = SC.create(SCD.category.place, SCD.type.virtual)
    static final SC place_virtual_site = SC.create(SCD.category.place, SCD.type.virtual, SCD.subtype.site)
    static final SC place_virtual_media = SC.create(SCD.category.place, SCD.type.virtual, SCD.subtype.media)
 
    static final SC organization = SC.create(SCD.category.organization)
    static final SC organization_administration = SC.create(SCD.category.organization, SCD.type.administration)
    static final SC organization_company = SC.create(SCD.category.organization, SCD.type.company)
    static final SC organization_institution = SC.create(SCD.category.organization, SCD.type.institution)
 
    static final SC event = SC.create(SCD.category.event)
    static final SC event_organized = SC.create(SCD.category.event, SCD.type.organized)
    static final SC event_happening = SC.create(SCD.category.event, SCD.type.happening)
    static final SC event_pastevent = SC.create(SCD.category.event, SCD.type.pastevent)
 
    static final SC masterpiece = SC.create(SCD.category.masterpiece)
    static final SC masterpiece_plan = SC.create(SCD.category.masterpiece, SCD.type.plan)
    static final SC masterpiece_reproduced = SC.create(SCD.category.masterpiece, SCD.type.reproduced)
    static final SC masterpiece_workofart = SC.create(SCD.category.masterpiece, SCD.type.workofart)
  
    static final SC abstraction = SC.create(SCD.category.abstraction)
    static final SC abstraction_name = SC.create(SCD.category.abstraction, SCD.type.name)
    static final SC abstraction_discipline = SC.create(SCD.category.abstraction, SCD.type.discipline)
    static final SC abstraction_state = SC.create(SCD.category.abstraction, SCD.type.state)
    static final SC abstraction_idea = SC.create(SCD.category.abstraction, SCD.type.idea)

    static final SC thing = SC.create(SCD.category.thing)
    static final SC thing_class = SC.create(SCD.category.thing, SCD.type.'class')    
    static final SC thing_memberclass = SC.create(SCD.category.thing, SCD.type.memberclass)    
    static final SC thing_object = SC.create(SCD.category.thing, SCD.type.object)   
    static final SC thing_substance = SC.create(SCD.category.thing, SCD.type.substance)   
    
    static final SC time = SC.create(SCD.category.time)
    static final SC time_calendar_date = SC.create(SCD.category.time, SCD.type.calendar, SCD.subtype.date)
    static final SC time_calendar_hour = SC.create(SCD.category.time, SCD.type.calendar, SCD.subtype.hour)
    static final SC time_calendar_interval = SC.create(SCD.category.time, SCD.type.calendar, SCD.subtype.interval)
    static final SC time_duration = SC.create(SCD.category.time, SCD.type.duration)
    static final SC time_frequency = SC.create(SCD.category.time, SCD.type.frequency)

    static final SC number = SC.create(SCD.category.number)
    static final SC number_cardinal = SC.create(SCD.category.number, SCD.type.cardinal)
    static final SC number_ordinal = SC.create(SCD.category.number, SCD.type.ordinal)
    static final SC number_textual = SC.create(SCD.category.number, SCD.type.textual)
    static final SC number_numeral = SC.create(SCD.category.number, SCD.type.numeral)

    static final SC value = SC.create(SCD.category.value)
    static final SC value_currency = SC.create(SCD.category.value, SCD.type.currency)
    static final SC value_quantity = SC.create(SCD.category.value, SCD.type.quantity)
    static final SC value_classification = SC.create(SCD.category.value, SCD.type.classification)
 
    static final SC unknown = SC.create(SCD.unknown)
    
}