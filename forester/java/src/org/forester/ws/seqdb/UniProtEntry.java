// $Id:
// forester -- software libraries and applications
// for genomics and evolutionary biology research.
//
// Copyright (C) 2010 Christian M Zmasek
// Copyright (C) 2010 Sanford-Burnham Medical Research Institute
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

package org.forester.ws.seqdb;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.forester.go.BasicGoTerm;
import org.forester.go.GoNameSpace;
import org.forester.go.GoTerm;
import org.forester.phylogeny.data.Accession;
import org.forester.util.ForesterUtil;

public final class UniProtEntry implements SequenceDatabaseEntry {

    public final static Pattern  BindingDB_PATTERN = Pattern.compile( "BindingDB;\\s+([0-9A-Z]+);" );
    public final static Pattern  CTD_PATTERN       = Pattern.compile( "CTD;\\s+(\\d+);" );
    public final static Pattern  DrugBank_PATTERN  = Pattern.compile( "DrugBank;\\s+([0-9A-Z]+);\\s+([^\\.]+)" );
    public final static Pattern  GO_PATTERN        = Pattern.compile( "GO;\\s+(GO:\\d+);\\s+([PFC]):([^;]+);" );
    public final static Pattern  KEGG_PATTERN      = Pattern.compile( "KEGG;\\s+([a-z]+:[0-9]+);" );
    public final static Pattern  MIM_PATTERN       = Pattern.compile( "MIM;\\s+(\\d+);" );
    public final static Pattern  NextBio_PATTERN   = Pattern.compile( "NextBio;\\s+(\\d+);" );
    public final static Pattern  Orphanet_PATTERN  = Pattern.compile( "Orphanet;\\s+(\\d+);\\s+([^\\.]+)" );
    public final static Pattern  PDB_PATTERN       = Pattern.compile( "PDB;\\s+([0-9A-Z]{4});\\s+([^;]+)" );
    public final static Pattern  PharmGKB_PATTERN  = Pattern.compile( "PharmGKB;\\s+([0-9A-Z]+);" );
    public final static Pattern  Reactome_PATTERN  = Pattern.compile( "Reactome;\\s+([0-9A-Z]+);\\s+([^\\.]+)" );
    private String               _ac;
    private ArrayList<Accession> _cross_references;
    private String               _gene_name;
    private List<GoTerm>         _go_terms;
    private String               _name;
    private String               _os_scientific_name;
    private String               _symbol;
    private String               _tax_id;

    private UniProtEntry() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public String getAccession() {
        return _ac;
    }

    @Override
    public List<Accession> getCrossReferences() {
        return _cross_references;
    }

    @Override
    public String getGeneName() {
        return _gene_name;
    }

    @Override
    public List<GoTerm> getGoTerms() {
        return _go_terms;
    }

    @Override
    public String getProvider() {
        return "uniprot";
    }

    @Override
    public String getSequenceName() {
        return _name;
    }

    @Override
    public String getSequenceSymbol() {
        return _symbol;
    }

    @Override
    public String getTaxonomyIdentifier() {
        return _tax_id;
    }

    @Override
    public String getTaxonomyScientificName() {
        return _os_scientific_name;
    }

    @Override
    public boolean isEmpty() {
        return ( ForesterUtil.isEmpty( getAccession() ) && ForesterUtil.isEmpty( getSequenceName() )
                && ForesterUtil.isEmpty( getTaxonomyScientificName() ) && ForesterUtil.isEmpty( getSequenceSymbol() )
                && ForesterUtil.isEmpty( getGeneName() ) && ForesterUtil.isEmpty( getTaxonomyIdentifier() )
                && ForesterUtil.isEmpty( getSequenceSymbol() ) && ( ( getGoTerms() == null ) || getGoTerms().isEmpty() ) && ( ( getCrossReferences() == null ) || getCrossReferences()
                .isEmpty() ) );
    }

    private void addCrossReference( final Accession accession ) {
        if ( _cross_references == null ) {
            _cross_references = new ArrayList<Accession>();
        }
        System.out.println( "XREF ADDED: " + accession );
        _cross_references.add( accession );
    }

    private void addGoTerm( final BasicGoTerm g ) {
        if ( _go_terms == null ) {
            _go_terms = new ArrayList<GoTerm>();
        }
        _go_terms.add( g );
    }

    private void setAc( final String ac ) {
        if ( _ac == null ) {
            _ac = ac;
        }
    }

    private void setGeneName( final String gene_name ) {
        if ( _gene_name == null ) {
            _gene_name = gene_name;
        }
    }

    private void setOsScientificName( final String os_scientific_name ) {
        if ( _os_scientific_name == null ) {
            _os_scientific_name = os_scientific_name;
        }
    }

    private void setSequenceName( final String name ) {
        if ( _name == null ) {
            _name = name;
        }
    }

    private void setSequenceSymbol( final String symbol ) {
        _symbol = symbol;
    }

    private void setTaxId( final String tax_id ) {
        if ( _tax_id == null ) {
            _tax_id = tax_id;
        }
    }

    public static SequenceDatabaseEntry createInstanceFromPlainText( final List<String> lines ) {
        final UniProtEntry e = new UniProtEntry();
        for( final String line : lines ) {
            //System.out.println( line );
            if ( line.startsWith( "AC" ) ) {
                e.setAc( SequenceDbWsTools.extractFromTo( line, "AC", ";" ) );
            }
            else if ( line.startsWith( "DE" ) && ForesterUtil.isEmpty( e.getSequenceName() ) ) {
                if ( ( line.indexOf( "RecName:" ) > 0 ) && ( line.indexOf( "Full=" ) > 0 ) ) {
                    e.setSequenceName( SequenceDbWsTools.extractFromTo( line, "Full=", ";" ) );
                }
                else if ( ( line.indexOf( "SubName:" ) > 0 ) && ( line.indexOf( "Full=" ) > 0 ) ) {
                    e.setSequenceName( SequenceDbWsTools.extractFromTo( line, "Full=", ";" ) );
                }
            }
            else if ( line.startsWith( "DE" ) && ForesterUtil.isEmpty( e.getSequenceSymbol() ) ) {
                if ( line.indexOf( "Short=" ) > 0 ) {
                    e.setSequenceSymbol( SequenceDbWsTools.extractFromTo( line, "Short=", ";" ) );
                }
            }
            else if ( line.startsWith( "GN" ) && ForesterUtil.isEmpty( e.getGeneName() ) ) {
                if ( line.indexOf( "Name=" ) > 0 ) {
                    e.setGeneName( SequenceDbWsTools.extractFromTo( line, "Name=", ";" ) );
                }
            }
            else if ( line.startsWith( "DR" ) ) {
                if ( line.indexOf( "GO;" ) > 0 ) {
                    final Matcher m = GO_PATTERN.matcher( line );
                    if ( m.find() ) {
                        final String id = m.group( 1 );
                        final String ns_str = m.group( 2 );
                        final String desc = m.group( 3 );
                        String gns = GoNameSpace.BIOLOGICAL_PROCESS_STR;
                        if ( ns_str.equals( "F" ) ) {
                            gns = GoNameSpace.MOLECULAR_FUNCTION_STR;
                        }
                        else if ( ns_str.equals( "C" ) ) {
                            gns = GoNameSpace.CELLULAR_COMPONENT_STR;
                        }
                        e.addGoTerm( new BasicGoTerm( id, desc, gns, false ) );
                    }
                }
                else if ( line.indexOf( "PDB;" ) > 0 ) {
                    final Matcher m = PDB_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "PDB", m.group( 2 ) ) );
                    }
                }
                else if ( line.indexOf( "KEGG;" ) > 0 ) {
                    final Matcher m = KEGG_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "KEGG" ) );
                    }
                }
                else if ( line.indexOf( "CTD;" ) > 0 ) {
                    final Matcher m = CTD_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "CTD" ) );
                    }
                }
                else if ( line.indexOf( "MIM;" ) > 0 ) {
                    final Matcher m = MIM_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "MIM" ) );
                    }
                }
                else if ( line.indexOf( "Orphanet;" ) > 0 ) {
                    final Matcher m = Orphanet_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "Orphanet", m.group( 2 ) ) );
                    }
                }
                else if ( line.indexOf( "PharmGKB;" ) > 0 ) {
                    final Matcher m = PharmGKB_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "PharmGKB" ) );
                    }
                }
                else if ( line.indexOf( "BindingDB;" ) > 0 ) {
                    final Matcher m = BindingDB_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "BindingDB" ) );
                    }
                }
                else if ( line.indexOf( "DrugBank;" ) > 0 ) {
                    final Matcher m = DrugBank_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "DrugBank", m.group( 2 ) ) );
                    }
                }
                else if ( line.indexOf( "NextBio;" ) > 0 ) {
                    final Matcher m = NextBio_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "NextBio" ) );
                    }
                }
                else if ( line.indexOf( "Reactome;" ) > 0 ) {
                    final Matcher m = Reactome_PATTERN.matcher( line );
                    if ( m.find() ) {
                        e.addCrossReference( new Accession( m.group( 1 ), "Reactome", m.group( 2 ) ) );
                    }
                }
            }
            else if ( line.startsWith( "OS" ) ) {
                if ( line.indexOf( "(" ) > 0 ) {
                    e.setOsScientificName( SequenceDbWsTools.extractFromTo( line, "OS", "(" ) );
                }
                else {
                    e.setOsScientificName( SequenceDbWsTools.extractFromTo( line, "OS", "." ) );
                }
            }
            else if ( line.startsWith( "OX" ) ) {
                if ( line.indexOf( "NCBI_TaxID=" ) > 0 ) {
                    e.setTaxId( SequenceDbWsTools.extractFromTo( line, "NCBI_TaxID=", ";" ) );
                }
            }
        }
        return e;
    }
}
