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

package rembrandt.gazetteers.en

import rembrandt.gazetteers.Patterns
import rembrandt.obj.SemanticClassification
import rembrandt.gazetteers.SemanticClassificationDefinitions as Classes
/**
 * @author Nuno Cardoso
 * This class stores internal evidence definitions for NEs
 * and maps them to the corresponding classification categories.
 */
class InternalEvidenceDefinitionsEN {
    
    static final meanings = [
                
         /* *************
         /*  1 - LOCAL    
         * *************/
                
         /* LOCAL HUMANO PAÍS */
                
         /* LOCAL HUMANO DIVISÃO */
                
          // XXX City, etc.
         [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                  Classes.subtype.division), needle:[
           ~/[Cc]ity/, ~/[Cc]ounty/, ~/[Dd]istrict/, ~/[Mm]unicipality/, ~/[Ss]tate/,
           ~/[Tt]own/, ~/[Vv]illage/ ], reverse:true ] , 
                
          // City of XXX
         [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                  Classes.subtype.division), needle:[   
           [~/[Cc]ity/,'of'],[~/[Cc]ounty/,'of'],[~/[Dd]istrict/,'of'],[~/[Mm]unicipality/,'of'],
           [~/[Tt]own/,'of'], [~/[Ss]tate/,'of'], [~/[Vv]illage/,'of'] ] ], 

          /* LOCAL HUMANO REGIAO */
         [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                 Classes.subtype.region), needle:[ 
              ~/[Pp]rovince/, ~/[Rr]egion/ ], reverse:true],
                
          /* LOCAL HUMANO CONSTRUCAO */
         [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                Classes.subtype.construction), needle:[
         ~/[Aa]irport/, ~/[Aa]irstrip/, ~/[Aa]uditorium/, ~/[Aa]utodrome/, ~/[Bb]ridge/, 
         ~/[Cc]astle/, ~/[Cc]athedral/, ~/[Cc]hapel/, ~/[Cc]ircuit/, ~/[Cc]onvent/, ~/[Dd]am/,
         ~/[Ff]ortress/, ~/[Cc]hurch/, ~/[Gg]ardens?/, ~/[Hh]arbou?r/, ~/[Mm]onastary/, 
         ~/[Pp]alace/, ~/[Pp]ark/, ~/[Tt]rack/, ~/[Pp]ool/, ~/[Ss]tadium/, ~/[Ss]tudio/, 
         ~/[Tt]ower/, ~/[Zz]oo/ ], reverse:true],
                
         [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                Classes.subtype.construction), needle:[
          [~/[Aa]irport/,'of'], [~/[Aa]irstrip/,'of'], [~/[Aa]uditorium/,'of'], [~/[Aa]utodrome/,'of'], 
          [~/[Bb]ridge/,'of'], [~/[Cc]astle/,'of'],[~/[Cc]athedral/,'of'],[~/[Cc]hapel/,'of'], 
          [~/[Cc]ircuit/,'of'], [~/[Cc]onvent/,'of'], [~/[Dd]am/,'of'], [~/[Ff]ortress/,'of'], 
          [~/[Cc]hurch/,'of'], [~/[Gg]ardens?/,'of'], [~/[Hh]arbou?r/,'of'], [~/[Mm]onastary/,'of'], 
          [~/[Pp]alace/,'of'], [~/[Pp]ark/,'of'], [~/[Tt]rack/,'of'], [~/[Pp]ool/,'of'], [~/[Ss]tadium/,'of'], 
          [~/[Ss]tudio/,'of'], [~/[Tt]ower/,'of'], [~/[Zz]oo/,'of'] ] ],
                
                /* LOCAL HUMANO RUA */
         [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
               Classes.subtype.street), needle:[
         ~/[Hh]ighway/, ~/[Ff]reeway/, ~/[Aa]venue/, ~/[Aa]v\./, ~/[Aa]lley/, 
         ~/[Rr]oad/, ~/[Bb]oulevard/, ~/[Bb]lvd\./, ~/[Ss]quare/, ~/[Ss]q\./, 
         ~/[Ss]treet/, ~/[Ss]tr?\./ ], reverse:true],
                
         [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                 Classes.subtype.street), needle:[
          ~/[Hh]ighway/, ~/[Ff]reeway/, ~/[Aa]venue/, ~/[Aa]v\./, ~/[Aa]lley/, 
          ~/[Rr]oad/, ~/[Bb]oulevard/, ~/[Bb]lvd\./, ~/[Ss]quare/, ~/[Ss]q\./, 
          ~/[Ss]treet/, ~/[Ss]tr?\./ ] ],
        
        /* LOCAL HUMANO OUTRO */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                Classes.subtype.other), needle:[    
                ~/[Rr]oom/ ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.human, 
                Classes.subtype.other), needle:[    
                ~/[Rr]oom/ ] ],
        
        /* LOCAL FISICO AGUACURSO */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.watercourse), needle:[  
                [~/[Ee]ffluent/,'of'], [~/[Ww]aterfalls?/,'of'], [~/[Rr]iver/,'of'] ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.watercourse), needle:[  
                ~/[Ee]ffluent/, ~/[Ww]aterfalls?/, ~/[Ff]alls?/, ~/[Rr]iver/ ] ] ,
                
        /* LOCAL FISICO AGUAMASSA */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.watermass), needle:[  
                ~/[Bb]ay/, ~/[Cc]hannel/, ~/[Ss]traight/, ~/[Gg]ulf/, ~/[Ll]ake/, ~/[Ss]ea/, 
                ~/[Oo]cean/ ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.watermass), needle:[  
                [~/[Bb]ay/,'of'], [~/[Cc]hannel/,'of'], [~/[Ss]traight/,'of'], [~/[Gg]ulf/,'of'], 
                [~/[Ll]ake/,'of'], [~/[Ss]ea/,'of'], [~/[Oo]cean/,'of'] ] ],
        
        /* LOCAL FISICO RELEVO */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.mountain), needle:[  
                ~/[Bb]each/, ~/[Mm]ountains?/, ~/[Mm]ount/, ~/[Ss]ierra/, ~/[Pp]rairie/, 
                ~/[Cc]reek/, ~/[Pp]eak/, ~/[Vv]alley/, ~/[Vv]ulcanoe?s?/ ], reverse:true] ,   
            
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.mountain), needle:[  
                [~/[Bb]each/,'of'], [~/[Mm]ountains?/,'of'], [~/[Mm]ount/,'of'], [~/[Ss]ierra/,'of'], 
                [~/[Pp]rairie/,'of'], [~/[Cc]reek/,'of'], [~/[Pp]eak/,'of'], [~/[Vv]alley/,'of'], 
                [~/[Vv]ulcanoe?s?/,'of'] ] ] ,   
        
        /* LOCAL FISICO PLANETA */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.planet), needle:[   
                ~/[Cc]onstellation/, ~/[Pp]lanet/ ], reverse:true] ,
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.planet), needle:[   
                [~/[Cc]onstellation/,'of'], [~/[Pp]lanet/,'of'] ] ] ,
        
        /* LOCAL FISICO ILHA */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.island), needle:[  
                ~/[Ii]slands?/, ~/[Aa]rchipelago/ ], reverse:true] ,
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.island), needle:[  
                [~/[Ii]slands?/,'of'], [~/[Aa]rchipelago/,'of'] ] ] ,           
        
        /* LOCAL FISICO REGIAO */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.region), needle:[    
                ~/[Rr]egion/, ~/[Dd]esert/ ], reverse:true] ,  
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.region), needle:[    
                [~/[Rr]egion/,'of'], [~/[Dd]esert/,'of'] ] ]  , 
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.physical, 
                Classes.subtype.other), needle:[     
                ~/[Ff]orest/ ], reverse:true] ,         
        
        /* LOCAL VIRTUAL COMSOCIAL */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
                Classes.subtype.media), needle:[  
                ~/[Dd]iary/, ~/[Nn]ewspapers?/, ~/[Nn]ews/, ~/[Mm]agazine/, ~/[Tt]elevision/, 
                'TV' ] , reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
                Classes.subtype.media), needle:[  
                ~/[Rr]adio/, ~/[Cc]hannel/ ] ],
        
        /* LOCAL VIRTUAL SITIO */
        [answer:SemanticClassification.create(Classes.category.place, Classes.type.virtual, 
                Classes.subtype.site), needle:[ 
                ~/[Oo]nline/, ~/[Ww]ebpage/, ~/[Ss]ite/, ~/[Nn]et/, ~/[Ww]ebsite/ ], reverse:true], 
            
        /* LOCAL VIRTUAL OBRA */
                
         /**************
         /* 2 - PESSOA *   
         **************/
                
        /* PESSOA INDIVIDUAL */
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.individual), needle:[
                //Dr Dr. Drs. Mrs. Mrs Mr Mr. Sir Sirs Miss 
                ~/[DdMm]rs?\.?/, ~/[Ss]irs?/, ~/[Mm]iss/, ~/[Dd]octors?/, ~/[Dd]oc\./, ~/[Pp]rof\./, 
                ~/[Pp]rofessors?/, 'Pope', ['His','Holiness'], [~/H(?:is|er)/,/[Mm]ajesty/], 
                [~/H(?:is|er)/,'Royal','Highness'] ] ],
        
        //XXX, PhD.
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.individual), needle:[
                [~/,/,~/[Pp]h[Dd]\.?/].reverse() ], reverse:true],
        
        // Profissão/Cargo normal
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.individual), needle:[
                [~/[Cc]hief/, Patterns.CapitalizedAlphaNumWord], 
                [~/[Cc]omissioner/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Cc]ount(?:ess)?/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Dd]irec?tors?/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Cc]ongress(?:wo)?man/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Dd]u(?:ke|chess)/,Patterns.CapitalizedAlphaNumWord], 
                [~/(?:[Ee]x-)?[Pp]resident/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Hh]igh/,/[Cc]omissioner/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Hh]igh-[Cc]omissioner/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Mm]anager/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Gg]overnor/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Jj]udge/,Patterns.CapitalizedAlphaNumWord], 
                [~/[Mm]arquise|[Mm]archioness/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Pp]riest/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Pp]remi[êe]r?/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Pp]rime/,/[Mm]inister/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Pp]rime-[Mm]inister/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Mm]inist[oa]/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Qq]ueen/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Kk]ing/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Pp]rince(?:ss)?/,Patterns.CapitalizedAlphaNumWord],  
                [~/[Ss]ecretary/,Patterns.CapitalizedAlphaNumWord] ]] ,   
                
        // Profissão/Cargo normal
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.position), needle:[
                [~/[Cc]hief/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Cc]omissioner/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Cc]ount(?:ess)?/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Dd]irector/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Cc]ongress(?:wo)?man/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Dd]u(?:ke|chess)/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/(?:[Ee]x-)?[Pp]resident/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Hh]igh/,/[Cc]omissioner/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Hh]igh-[Cc]omissioner/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Mm]anager/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Gg]overnor/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Jj]udge/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Mm]arquise|[Mm]archioness/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]riest/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]remi[êe]r?/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]rime/,/[Mm]inister/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]rime-[Mm]inister/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Mm]inister/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Qq]ueen/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Kk]ing/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]rince(?:ss)?/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Ss]ecretary/,Patterns.NotCapitalizedAlphaNumWord] ]] , 
        
        // Profissão/Cargo normal
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.positiongroup), needle:[
                [~/[Cc]hiefs/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Cc]omissioners/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Cc]ount(?:ess)e?s/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Dd]irectors/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Cc]ongress(?:wo)?mans?/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Dd]u(?:ke|chess)e?s/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/(?:[Ee]x-)?[Pp]residents/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Hh]igh/,/[Cc]omissioners/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Hh]igh-[Cc]omissioners/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Mm]anagers/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Gg]overnors/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Jj]udges/,Patterns.NotCapitalizedAlphaNumWord], 
                [~/[Mm]arquises|[Mm]archionesses/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]riests/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]remi[êe]r?s/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]rime/,/[Mm]inisters/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]rime-[Mm]inisters/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Mm]inisters/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Qq]ueens/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Kk]ings/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Pp]rince(?:ss)?e?s/,Patterns.NotCapitalizedAlphaNumWord],  
                [~/[Ss]ecretaries/,Patterns.NotCapitalizedAlphaNumWord] ]]  ,       
        
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.membergroup) , needle:[
                ~/[Tt]eam/ ]],
        
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.membergroup) , needle:[
                ~/[Tt]eam/ ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.individualgroup) , needle:[
                ~/[Ff]amily/ ]],
        
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.individualgroup) , needle:[
                ~/[Ff]amily/ ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.person, Classes.type.people) , needle:[
                [~/[Pp]eople/,'of'] ]],
        
        /*******************
         /* 3 - ORGANIZACAO *   
         *******************/
        
        /* ORGANIZACAO EMPRESA */
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.company), needle:[
                ~/Inc\.?/, ['Assurance','Quality'], ~/Corp\.?/, 'Corporation' ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.company), needle:[     
                ~/[Cc]ompany/, [~/[Ss]hopping/,/[Cc]ent(?:er|re)/], ~/[Cc]inema/,
                ~/[Cc]asino/, ~/[Tt]heat(?:re|er)/, ~/[Gg]allery/, ~/[HhMm]otel/, 
                ~/[Mm]useum/, ~/[Rr]estaurant/, ~/[Ss]hopping/ ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.company), needle:[    
                ~/[Cc]ompany/, [~/[Ss]hopping/,/[Cc]ent(?:er|re)/], ~/[Cc]inema/,
                ~/[Cc]asino/, ~/[Tt]heat(?:re|er)/, ~/[Gg]allery/, ~/[HhMm]otel/, 
                ~/[Rr]estaurant/, ~/[Ss]hopping/ ] ],
        
        /* ORGANIZACAO INSTITUICAO */
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.institution), needle:[
                ~/[Aa]ssociation/, ~/[Aa]cademy/, ~/[Aa]quarium/, ~/[Aa]gency/, 
                [~/[Fi]re/, ~/[Bb]rigade/], ~/[Gg]uild/, ~/[Ll]ibrary/, 
                [~/[Nn]ational/, ~/[Cc]ompany/], ~/[Cc]omitee?/, ~/[Cc]onfederation/,  
                [~/[Rr]ed/, ~/[Cc]ross/], ~/[Cc]ollege/, ~/[Ff]aculty/,  
                ~/[Ff]ederation/, ~/[Ff]oundation/, ~/[Hh]ospital/, ~/[Ii]nstitute/, ~/[Ll]yceum/,
                ~/[Ll]egion/, ~/[Pp]arty/, ~/[Pp]olice/, ~/[Ss]yndicate/, ~/[Ss]chool/,
                ~/[Uu]niversity/ ] ],
        
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.institution), needle:[      
                ~/[Aa]ssociation/, ~/[Aa]cademy/, ~/[Aa]quarium/, ~/[Aa]gency/, 
                [~/[Fi]re/, ~/[Bb]rigade/].reverse(), ~/[Gg]uild/, ~/[Ll]ibrary/, 
                [~/[Nn]ational/, ~/[Cc]ompany/].reverse(), ~/[Cc]omitee/, ~/[Cc]onfederation/,  
                [~/[Rr]ed/, ~/[Cc]ross/].reverse(), ~/[Cc]ollege/, ~/[Ss]chool/, ~/[Ff]aculty/,  
                ~/[Ff]ederation/, ~/[Ff]oundation/, ~/[Hh]igh/, ~/[Hh]ospital/, ~/[Ii]nstitute/, ~/[Ll]yceum/,
                ~/[Ll]egion/, ~/[Pp]arty/, ~/[Pp]olice/, ~/[Ss]yndicate/,
                ~/[Uu]niversity/], reverse:true],
        
        //  [answer:SemanticClassification.create('ORGANIZACAO', 'INSTITUICAO', sub"SUB"]
        
        /* ORGANIZACAO ADMINISTRACAO */
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.administration), needle:[
                ~/[Aa]ssembly/, ~/[Aa]rmy/, [~/[Aa]ir/, ~/[Ff]orce/], ~/[Aa]dministration/, ~/[Bb]ureau/,
                [~/[Cc]ity/, ~/[Hh]all/].reverse(), ~/[Cc]omissariate/, ~/[Cc]ouncil/, ~/[Gg]overnment/, 
                ~/[Nn]avy/, ~/[Mm]inistery/, ~/[Pp]arliament/,  [~/[Ss]ecretary/,'of', ~/[Ss]tate/].reverse() 
                ], reverse:true],
        
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.administration), needle:[
                ~/[Aa]ssembly/, ~/[Aa]rmy/, [~/[Aa]ir/,~/[Ff]orce/], ~/[Aa]dministration/, 
                ~/[Bb]ureau/, [~/[Cc]ity/, ~/[Hh]all/], ~/[Cc]omissariate/, ~/[Cc]ouncil/, ~/[Gg]overnment/, 
                ~/[Nn]avy/, ~/[Mm]inistery/, ~/[Pp]arliament/, [~/[Ss]ecretary/, 'of', ~/[Ss]tate/]
                ] ] ,     
        
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.other), needle:[
                ~/[Cc]ent(?:er|re)/, ~/[Cc]omission/, ~/[Dd]ivision/, ~/[Dd]epartment/, 
                ~/[Dd]e?pt?\.?/, ~/[Ll]aboratory/, ~/[Pp]anel/, ~/[Ss]ector/, ~/[Ss]ecretariate/, 
                ~/[Ss]ection/, ~/[Ss]ervices/, ~/[Uu]nion/, ~/[Uu]nit/] ] ,     
        
        [answer:SemanticClassification.create(Classes.category.organization, Classes.type.other), needle:[
                ~/[Cc]ent(?:er|re)/, ~/[Cc]omission/, ~/[Dd]ivision/, ~/[Dd]epartment/, 
                ~/[Dd]e?pt?\.?/, ~/[Ll]aboratory/, ~/[Pp]anel/, ~/[Ss]ector/, ~/[Ss]ecretariate/, 
                ~/[Ss]ection/, ~/[Ss]ervices/, ~/[Uu]nion/, ~/[Uu]nit/], reverse:true ],
        
        /**********************
 /* 6 - ACONTECIMENTO  *   
 **********************/    
        
        /* ACONTECIMENTO EFEMERIDE */
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.happening), needle:[
                ~/[Bb]attle/, ~/[Cc]apitulation/, [~/[Ww]orld/,/[Dd]ay/].reverse(), ~/[Ww]ar/, 
                ~/[Ii]ndependence/, ~/[Rr]estauration/, ~/[Rr]evolution/ ], reverse:true],
        
        /* ACONTECIMENTO EFEMERIDE */
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.happening), needle:[
                [~/[Bb]attle/,'of'], [~/[Cc]apitulation/,'of'], [~/[Ww]orld/,/[Dd]ay/,'of'], 
                [~/[Ww]ar/,'of'], [~/[Ii]ndependence/,'of'], [~/[Rr]estauration/,'of'], 
                [~/[Rr]evolution/,'of'] ] ],
        
        /* ACONTECIMENTO ORGANIZADO */
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.organized), needle:[
                ~/[Cc]ongress/, ~/[Cc]onference/, ~/[Cc]oncilium/, ~/[Cc]hampionship/, 
                ~/[Ss]ummit/, ~/[Ll]eague/, ~/[Ff]estival/, ~/[Mm]eeting/,  
                ~/[Gg]ames/, ~/[Jj]ourneys/, ~/[Rr]eunion/, ~/[Cc]up/,  
                ~/[Ss]eminar/ ] ],
        
        /* ACONTECIMENTO ORGANIZADO */
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.organized), needle:[
                ~/[Cc]ongress/, ~/[Cc]onference/, ~/[Cc]oncilium/, ~/[Cc]hampionship/, 
                ~/[Ss]ummit/, ~/[Ll]eague/, ~/[Ff]estival/,  
                ~/[Gg]ames/, ~/[Rr]eunion/, ~/[Cc]up/,  
                ~/[Ss]eminar/ ], reverse:true],
        
        /* ACONTECIMENTO EVENTO */
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.pastevent), needle:[      
                [NumberGazetteerEN.eventNumber, /[Cc]ongress/], 
                [NumberGazetteerEN.eventNumber, /[Cc]onference/], 
                [NumberGazetteerEN.eventNumber, /[Cc]oncilium/], 
                [NumberGazetteerEN.eventNumber, /[Cc]hampionship/], 
                [NumberGazetteerEN.eventNumber, /[Ss]ummit/],   
                [NumberGazetteerEN.eventNumber, /[Ll]eague/], 
                [NumberGazetteerEN.eventNumber, /[Ff]estival/],  
                [NumberGazetteerEN.eventNumber, /[Gg]ames/],  
                [NumberGazetteerEN.eventNumber, /[Mm]arathon/], 
                [NumberGazetteerEN.eventNumber, /[Mm]eeting/], 
                [NumberGazetteerEN.eventNumber, /[Rr]eunion/],  
                [NumberGazetteerEN.eventNumber, /[Ss]eminar/] ] ],
        
        [answer:SemanticClassification.create(Classes.category.event, Classes.type.pastevent), needle:[             
                [~/[Gg]rand/, ~/[Pp]rix/], ~/[Mm]arathon/ ], reverse:true],
        
        /******************
 /* 7 - ABSTRACCAO *   
 ******************/   
        
        /* ABSTRACCAO DISCIPLINA */ 
        [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.discipline), needle:[
                ~/[Ss]cience/, ~/[Dd]efense/,   
                ~/[Ll]aw/, ~/[Ee]conomy/, ~/[Ee]ducation/, ~/[Nn]ursery/, 
                ~/[Ee]ducation/, ~/[Ee]ngineering/, ~/[Ss]tatistics?/, ~/[Pp]hilosophy/,
                ~/[Ff]inances/, ~/[Mm]anagement/, ~/[Jj]ustice/,
                ~/[Ll]iterature/, ~/[Ll]ogic/, ~/[Mm]ath(matic)?/, ~/[Mm]arketing/, 
                ~/[Mm]edicine/, ~/[Mm]ining/, ~/[Oo]ptics/, 
                ~/[Cc]hemistry/, ~/[Pp]sychology/, ~/[Tt]echnology/, ~/[Tt]ourism/ ] ],
        
        /* ABSTRACCAO ESTADO */ // Abrange antiga ESCOLA e OBRA
        [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.state), needle:[
                ~/[Dd]isease/, ~/[Dd]eformation/, ~/[Ss]yndrome/ ] ],   
        
        [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.state), needle:[
                ~/[Dd]isease/, ~/[Dd]eformation/, ~/[Ss]yndrome/ ], reverse:true],
        
        /* ABSTRACCAO IDEIA */
        [answer:SemanticClassification.create(Classes.category.abstraction, Classes.type.idea), needle:[       
                ~/[Cc]omunism/, ~/[Ii]mpressionism/, ~/[Nn]eoliberalism/ ] ],
        
        /*************
 /* 8 - COISA *   
 *************/  
        
        /* COISA CLASSE */
        [answer:SemanticClassification.create(Classes.category.thing, Classes.type.'class'), needle:[
                ~/[Pp]rize/ ], reverse:true],
        
        /* COISA OBJECTO */
        // [answer:SemanticClassification.create('COISA', 'OBJECTO/], needle:[
        
        /* COISA SUBSTANCIA */
        // [answer:SemanticClassification.create('COISA', 'SUBSTANCIA/], needle:[
        
        /************
 /* 9 - OBRA *   
 ************/  
        
        /* OBRA PLANO */
        [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.plan), needle:[
                ~/[Aa]rticle/, ~/[Aa]greement/, ~/[Cc]ode/, 
                ~/[Dd]eclaration/, ~/[Dd]irective/, ~/[Dd]ecree/, // [~/[Bb]ill/],
                ~/[Tt]ax/, ~/[Ll]aw/, ~/[Bb]udget/, ~/[Pp]roject/,
                ~/[Pp]lan/, ~/[Pp]rogram/, ~/[Pp]rotocol/, 
                ~/[Rr]ule/, ~/[Ss]ystem/, ~/[Tt]reaty/ ], reverse:true],
        
        /* OBRA PLANO */
        [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.plan), needle:[
                ~/[Aa]rticle/, ~/[Aa]greement/, ~/[Cc]ode/, 
                ~/[Dd]eclaration/, ~/[Dd]irective/, ~/[Dd]ecree/, ~/Bill/,
                ~/[Tt]ax/, ~/[Ll]aw/, ~/[Bb]udget/, ~/[Pp]roject/,
                ~/[Pp]lan/, ~/[Pp]rogram/, ~/[Pp]rotocol/, 
                ~/[Rr]ule/, ~/[Ss]ystem/, ~/[Tt]reaty/ ] ] ,      
        
        /* OBRA REPRODUZIDA */
        [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.reproduced), needle:[
                ~/[Bb]ook/, ~/[Mm]ovie/ ] ] ,
        
        /* OBRA REPRODUZIDA */
        [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.reproduced), needle:[
                ~/[Bb]ook/, ~/[Mm]ovie/ ], reverse:true],     
        
        /* OBRA ARTE */
        [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.workofart), needle:[
                ~/[Ss]tatue/ ] ],
        
        /* OBRA ARTE */
        [answer:SemanticClassification.create(Classes.category.masterpiece, Classes.type.workofart), needle:[
                ~/[Ss]tatue/ ], reverse:true]
                
        ]
    
}