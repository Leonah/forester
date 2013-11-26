// $Id:
// Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.forester.protein.BinaryDomainCombination;
import org.forester.util.ForesterUtil;

public class BasicBinaryDomainCombination implements BinaryDomainCombination {

    final static Map<Short, String> id2str = new HashMap<Short, String>();
    final static Map<String, Short> str2id = new HashMap<String, Short>();
    static short                    count  = 0;
    short                           _id0;
    short                           _id1;

    static short getId( final String id ) {
        if ( !str2id.containsKey( id ) ) {
            if ( count >= Short.MAX_VALUE ) {
                throw new RuntimeException( "too many domain ids!" );
            }
            id2str.put( count, id );
            str2id.put( id, count );
            ++count;
        }
        return  str2id.get( id ) ;
    }

     static String getStr( final short id ) {
        return id2str.get( id );
    }

    public BasicBinaryDomainCombination( final String id0, final String id1 ) {
        if ( ( id0 == null ) || ( id1 == null ) ) {
            throw new IllegalArgumentException( "attempt to create binary domain combination using null" );
        }
        if ( ( id0.indexOf( SEPARATOR ) != -1 ) || ( id1.indexOf( SEPARATOR ) != -1 ) ) {
            throw new IllegalArgumentException( "ill formatted domain id: " + id0 + ", " + id1 );
        }
        if ( id0.toLowerCase().compareTo( id1.toLowerCase() ) < 0 ) {
           _id0 = getId( id0 );
           _id1 = getId( id1 );
        }
        else {
            // _data = id1 + SEPARATOR + id0;
            _id0 = getId( id1 );
            _id1 = getId( id0 );
        }
    }

    BasicBinaryDomainCombination() {
        _id0 = -1;
        _id1 = -1;
    }

    @Override
    public int compareTo( final BinaryDomainCombination binary_domain_combination ) {
        if ( binary_domain_combination.getClass() != this.getClass() ) {
            throw new IllegalArgumentException( "attempt to compare [" + binary_domain_combination.getClass() + "] to "
                    + "[" + this.getClass() + "]" );
        }
        if ( equals( binary_domain_combination ) ) {
            return 0;
        }
        final int x = getId0().compareTo( binary_domain_combination.getId0() );
        if ( x != 0 ) {
            return x;
        }
        else {
            return getId1().compareTo( binary_domain_combination.getId1() );
        }
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        else if ( o == null ) {
            throw new IllegalArgumentException( "attempt to check [" + this.getClass() + "] equality to null" );
        }
        else if ( o.getClass() != this.getClass() ) {
            throw new IllegalArgumentException( "attempt to check [" + this.getClass() + "] equality to ["
                    + o.getClass() + "]" );
        }
        else {
            return ( getId0().equals( ( ( BinaryDomainCombination ) o ).getId0() ) )
                    && ( getId1().equals( ( ( BinaryDomainCombination ) o ).getId1() ) );
        }
    }

    @Override
    public String getId0() {
        //return _data.substring( 0, _data.indexOf( SEPARATOR ) );
        return getStr( _id0 );
    }

    @Override
    public String getId1() {
        //return _data.substring( _data.indexOf( SEPARATOR ) + 1 );
        return getStr( _id1 );
    }

    @Override
    public int hashCode() {
        return getAsStr().hashCode();
    }

    @Override
    public StringBuffer toGraphDescribingLanguage( final OutputFormat format,
                                                   final String node_attribute,
                                                   final String edge_attribute ) {
        final StringBuffer sb = new StringBuffer();
        switch ( format ) {
            case DOT:
                if ( ForesterUtil.isEmpty( node_attribute ) ) {
                    sb.append( getId0() );
                    sb.append( " -- " );
                    sb.append( getId1() );
                    if ( !ForesterUtil.isEmpty( edge_attribute ) ) {
                        sb.append( " " );
                        sb.append( edge_attribute );
                    }
                    sb.append( ";" );
                }
                else {
                    sb.append( getId0() );
                    sb.append( " " );
                    sb.append( node_attribute );
                    sb.append( ";" );
                    sb.append( ForesterUtil.LINE_SEPARATOR );
                    sb.append( getId1() );
                    sb.append( " " );
                    sb.append( node_attribute );
                    sb.append( ";" );
                    sb.append( ForesterUtil.LINE_SEPARATOR );
                    sb.append( getId0() );
                    sb.append( " -- " );
                    sb.append( getId1() );
                    if ( !ForesterUtil.isEmpty( edge_attribute ) ) {
                        sb.append( " " );
                        sb.append( edge_attribute );
                    }
                    sb.append( ";" );
                }
                break;
            default:
                throw new AssertionError( "unknown format:" + format );
        }
        return sb;
    }

    @Override
    public String toString() {
        return getAsStr();
    }

    private String getAsStr() {
        return getId0() + SEPARATOR + getId1();
    }

    public static BinaryDomainCombination createInstance( final String ids ) {
        if ( ids.indexOf( BinaryDomainCombination.SEPARATOR ) < 1 ) {
            throw new IllegalArgumentException( "Unexpected format for binary domain combination [" + ids + "]" );
        }
        final String[] ids_ary = ids.split( BinaryDomainCombination.SEPARATOR );
        if ( ids_ary.length != 2 ) {
            throw new IllegalArgumentException( "Unexpected format for binary domain combination [" + ids + "]" );
        }
        return new BasicBinaryDomainCombination( ids_ary[ 0 ], ids_ary[ 1 ] );
    }
}
