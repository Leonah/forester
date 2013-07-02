// $Id:
// 22:09:42 cmzmasek Exp $
//
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2009 Christian M. Zmasek
// Copyright (C) 2008-2009 Burnham Institute for Medical Research
// All rights reserved
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
//
// Contact: phylosoft @ gmail . com
// WWW: https://sites.google.com/site/cmzmasek/home/software/forester

package org.forester.surfacing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.forester.util.DescriptiveStatistics;

class PrintableSpeciesSpecificDomainSimilariyData implements SpeciesSpecificDomainSimilariyData {

    private final static NumberFormat   FORMATTER = new DecimalFormat( "0.0E0" );
    final SortedMap<String, Integer>    _combinable_domain_id_to_count_map;
    final private int                   _key_domain_proteins_count;
    final private int                   _key_domain_domains_count;
    final private int                   _combinable_domains_count;
    final private DescriptiveStatistics _key_domain_confidence_descriptive_statistics;

    public PrintableSpeciesSpecificDomainSimilariyData( final int key_domain_proteins_count,
                                                        final int key_domain_domains_count,
                                                        final int combinable_domains,
                                                        final DescriptiveStatistics key_domain_confidence_descriptive_statistics ) {
        _key_domain_proteins_count = key_domain_proteins_count;
        _key_domain_domains_count = key_domain_domains_count;
        _combinable_domains_count = combinable_domains;
        _key_domain_confidence_descriptive_statistics = key_domain_confidence_descriptive_statistics;
        _combinable_domain_id_to_count_map = new TreeMap<String, Integer>();
    }

    @Override
    public void addProteinsExhibitingCombinationCount( final String domain_id, final int count ) {
        if ( getCombinableDomainIdToCountsMap().containsKey( domain_id ) ) {
            throw new IllegalArgumentException( "Domain with id " + domain_id + " already exists" );
        }
        getCombinableDomainIdToCountsMap().put( domain_id, count );
    }

    @Override
    public SortedMap<String, Integer> getCombinableDomainIdToCountsMap() {
        return _combinable_domain_id_to_count_map;
    }

    private int getCombinableDomainsCount() {
        return _combinable_domains_count;
    }

    private DescriptiveStatistics getKeyDomainConfidenceDescriptiveStatistics() {
        return _key_domain_confidence_descriptive_statistics;
    }

    private int getKeyDomainDomainsCount() {
        return _key_domain_domains_count;
    }

    private int getKeyDomainProteinsCount() {
        return _key_domain_proteins_count;
    }

    @Override
    public int getNumberOfProteinsExhibitingCombinationWith( final String domain_id ) {
        if ( !getCombinableDomainIdToCountsMap().containsKey( domain_id ) ) {
            throw new IllegalArgumentException( "Domain with id " + domain_id + " not found" );
        }
        return getCombinableDomainIdToCountsMap().get( domain_id );
    }

    @Override
    public String toString() {
        return toStringBuffer( DomainSimilarityCalculator.Detailedness.LIST_COMBINING_DOMAIN_FOR_EACH_SPECIES, false )
                .toString();
    }

    @Override
    public StringBuffer toStringBuffer( final DomainSimilarityCalculator.Detailedness detailedness, final boolean html ) {
        final StringBuffer sb = new StringBuffer();
        if ( detailedness == DomainSimilarityCalculator.Detailedness.PUNCTILIOUS ) {
            sb.append( " " );
            sb.append( getKeyDomainDomainsCount() );
            sb.append( ", " );
            sb.append( getKeyDomainProteinsCount() );
            sb.append( ", " );
            sb.append( getCombinableDomainsCount() );
            sb.append( ", " );
            if ( html ) {
                sb.append( "<i>" );
            }
            sb.append( FORMATTER.format( getKeyDomainConfidenceDescriptiveStatistics().arithmeticMean() ) );
            if ( html ) {
                sb.append( "</i>" );
            }
            if ( !getCombinableDomainIdToCountsMap().isEmpty() ) {
                sb.append( ":" );
            }
        }
        final Set<String> ids = getCombinableDomainIdToCountsMap().keySet();
        int i = 0;
        for( final String domain_id : ids ) {
            ++i;
            sb.append( " " );
            if ( html ) {
                sb.append( "<a href=\"" + SurfacingConstants.PFAM_FAMILY_ID_LINK + domain_id + "\">" + domain_id
                        + "</a>" );
            }
            else {
                sb.append( domain_id );
            }
            if ( detailedness == DomainSimilarityCalculator.Detailedness.PUNCTILIOUS ) {
                sb.append( ":" );
                sb.append( getCombinableDomainIdToCountsMap().get( domain_id ) );
            }
            if ( i < ( ids.size() - 1 ) ) {
                sb.append( "," );
            }
        }
        return sb;
    }
}
