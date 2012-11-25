
package org.forester.development;

public class neTest {

    public class DoublePointer {

        private double _value;

        DoublePointer( final double value ) {
            _value = value;
        }

        double getValue() {
            return _value;
        }

        void setValue( final double value ) {
            _value = value;
        }
    }
    double[][] eigvecs = new double[ 20 ][ 20 ];

    //globals
    //    void coeffs(double x, double y, double *c, double *s, double accuracy)
    //    { /* compute cosine and sine of theta */
    //      double root;
    //
    //      root = sqrt(x * x + y * y);
    //      if (root < accuracy) {
    //        *c = 1.0;
    //        *s = 0.0;
    //      } else {
    //        *c = x / root;
    //        *s = y / root;
    //      }
    //    }  /* coeffs */
    // compute cosine and sine of theta
    void coeffs( final double x, final double y, final DoublePointer c, final DoublePointer s, final double accuracy ) {
        final double root = Math.sqrt( ( x * x ) + ( y * y ) );
        if ( root < accuracy ) {
            c.setValue( 1.0 );
            s.setValue( 0.0 );
        }
        else {
            c.setValue( x / root );
            s.setValue( y / root );
        }
    }

    //    void tridiag(double (*a)[20], long n, double accuracy)
    //    { /* Givens tridiagonalization */
    //      long i, j;
    //      double s, c;
    //
    //      for (i = 2; i < n; i++) {
    //        for (j = i + 1; j <= n; j++) {
    //          coeffs(a[i - 2][i - 1], a[i - 2][j - 1], &c, &s,accuracy);
    //          givens(a, i, j, n, c, s, true);
    //          givens(a, i, j, n, c, s, false);
    //          givens(eigvecs, i, j, n, c, s, true);
    //        }
    //      }
    //    }  /* tridiag */
    // Givens tridiagonalization 
    void tridiag( final double a[][], final int n, final double accuracy ) {
        int i, j;
        double s, c;
        final DoublePointer sp = new DoublePointer( 0 );
        final DoublePointer cp = new DoublePointer( 0 );
        for( i = 2; i < n; i++ ) {
            for( j = i + 1; j <= n; j++ ) {
                coeffs( a[ i - 2 ][ i - 1 ], a[ i - 2 ][ j - 1 ], cp, sp, accuracy );
                c = cp.getValue();
                s = sp.getValue();
                givens( a, i, j, n, c, s, true );
                givens( a, i, j, n, c, s, false );
                givens( eigvecs, i, j, n, c, s, true );
            }
        }
    } /* tridiag */

    //    void shiftqr(double (*a)[20], long n, double accuracy)
    //    { /* QR eigenvalue-finder */
    //      long i, j;
    //      double approx, s, c, d, TEMP, TEMP1;
    //
    //      for (i = n; i >= 2; i--) {
    //        do {
    //          TEMP = a[i - 2][i - 2] - a[i - 1][i - 1];
    //          TEMP1 = a[i - 1][i - 2];
    //          d = sqrt(TEMP * TEMP + TEMP1 * TEMP1);
    //          approx = a[i - 2][i - 2] + a[i - 1][i - 1];
    //          if (a[i - 1][i - 1] < a[i - 2][i - 2])
    //            approx = (approx - d) / 2.0;
    //          else
    //            approx = (approx + d) / 2.0;
    //          for (j = 0; j < i; j++)
    //            a[j][j] -= approx;
    //          for (j = 1; j < i; j++) {
    //            coeffs(a[j - 1][j - 1], a[j][j - 1], &c, &s, accuracy);
    //            givens(a, j, j + 1, i, c, s, true);
    //            givens(a, j, j + 1, i, c, s, false);
    //            givens(eigvecs, j, j + 1, n, c, s, true);
    //          }
    //          for (j = 0; j < i; j++)
    //            a[j][j] += approx;
    //        } while (fabs(a[i - 1][i - 2]) > accuracy);
    //      }
    //    }  /* shiftqr */
    //
    // QR eigenvalue-finder 
    void shiftqr( final double a[][], final int n, final double accuracy ) {
        int i, j;
        double approx;
        final DoublePointer sp = new DoublePointer( 0 );
        final DoublePointer cp = new DoublePointer( 0 );
        double s;
        double c;
        double d;
        double TEMP;
        double TEMP1;
        for( i = n; i >= 2; i-- ) {
            do {
                TEMP = a[ i - 2 ][ i - 2 ] - a[ i - 1 ][ i - 1 ];
                TEMP1 = a[ i - 1 ][ i - 2 ];
                d = Math.sqrt( ( TEMP * TEMP ) + ( TEMP1 * TEMP1 ) );
                approx = a[ i - 2 ][ i - 2 ] + a[ i - 1 ][ i - 1 ];
                if ( a[ i - 1 ][ i - 1 ] < a[ i - 2 ][ i - 2 ] ) {
                    approx = ( approx - d ) / 2.0;
                }
                else {
                    approx = ( approx + d ) / 2.0;
                }
                for( j = 0; j < i; j++ ) {
                    a[ j ][ j ] -= approx;
                }
                for( j = 1; j < i; j++ ) {
                    coeffs( a[ j - 1 ][ j - 1 ], a[ j ][ j - 1 ], cp, sp, accuracy );
                    c = cp.getValue();
                    s = sp.getValue();
                    givens( a, j, j + 1, i, c, s, true );
                    givens( a, j, j + 1, i, c, s, false );
                    givens( eigvecs, j, j + 1, n, c, s, true );
                }
                for( j = 0; j < i; j++ ) {
                    a[ j ][ j ] += approx;
                }
            } while ( Math.abs( a[ i - 1 ][ i - 2 ] ) > accuracy );
        }
    } /* shiftqr */

    //    void givens(double (*a)[20], long i, long j, long n, double ctheta,
    //                double stheta, boolean left)
    //{ /* Givens transform at i,j for 1..n with angle theta */
    //long k;
    //double d;
    //
    //for (k = 0; k < n; k++) {
    //if (left) {
    //d = ctheta * a[i - 1][k] + stheta * a[j - 1][k];
    //a[j - 1][k] = ctheta * a[j - 1][k] - stheta * a[i - 1][k];
    //a[i - 1][k] = d;
    //} else {
    //d = ctheta * a[k][i - 1] + stheta * a[k][j - 1];
    //a[k][j - 1] = ctheta * a[k][j - 1] - stheta * a[k][i - 1];
    //a[k][i - 1] = d;
    //}
    //}
    //}  /* givens */
    //
    // Givens transform at i,j for 1..n with angle theta 
    void givens( final double a[][],
                 final int i,
                 final int j,
                 final int n,
                 final double ctheta,
                 final double stheta,
                 final boolean left ) {
        int k;
        double d;
        for( k = 0; k < n; k++ ) {
            if ( left ) {
                d = ( ctheta * a[ i - 1 ][ k ] ) + ( stheta * a[ j - 1 ][ k ] );
                a[ j - 1 ][ k ] = ( ctheta * a[ j - 1 ][ k ] ) - ( stheta * a[ i - 1 ][ k ] );
                a[ i - 1 ][ k ] = d;
            }
            else {
                d = ( ctheta * a[ k ][ i - 1 ] ) + ( stheta * a[ k ][ j - 1 ] );
                a[ k ][ j - 1 ] = ( ctheta * a[ k ][ j - 1 ] ) - ( stheta * a[ k ][ i - 1 ] );
                a[ k ][ i - 1 ] = d;
            }
        }
    }
    // this jtt matrix decomposition due to Elisabeth  Tillier 
    final private static double jtteigs[]    = { +0.00000000000000, -1.81721720738768, -1.87965834528616,
            -1.61403121885431, -1.53896608443751, -1.40486966367848, -1.30995061286931, -1.24668414819041,
            -1.17179756521289, -0.31033320987464, -0.34602837857034, -1.06031718484613, -0.99900602987105,
            -0.45576774888948, -0.86014403434677, -0.54569432735296, -0.76866956571861, -0.60593589295327,
            -0.65119724379348, -0.70249806480753 };
    final private static double jttprobs[][] = {
            { +0.07686196156903, +0.05105697447152, +0.04254597872702, +0.05126897436552, +0.02027898986051,
            +0.04106097946952, +0.06181996909002, +0.07471396264303, +0.02298298850851, +0.05256897371552,
            +0.09111095444453, +0.05949797025102, +0.02341398829301, +0.04052997973502, +0.05053197473402,
            +0.06822496588753, +0.05851797074102, +0.01433599283201, +0.03230298384851, +0.06637396681302 },
            { -0.04445795120462, -0.01557336502860, -0.09314817363516, +0.04411372100382, -0.00511178725134,
            +0.00188472427522, -0.02176250428454, -0.01330231089224, +0.01004072641973, +0.02707838224285,
            -0.00785039050721, +0.02238829876349, +0.00257470703483, -0.00510311699563, -0.01727154263346,
            +0.20074235330882, -0.07236268502973, -0.00012690116016, -0.00215974664431, -0.01059243778174 },
            { +0.09480046389131, +0.00082658405814, +0.01530023104155, -0.00639909042723, +0.00160605602061,
            +0.00035896642912, +0.00199161318384, -0.00220482855717, -0.00112601328033, +0.14840201765438,
            -0.00344295714983, -0.00123976286718, -0.00439399942758, +0.00032478785709, -0.00104270266394,
            -0.02596605592109, -0.05645800566901, +0.00022319903170, -0.00022792271829, -0.16133258048606 },
            { -0.06924141195400, -0.01816245289173, -0.08104005811201, +0.08985697111009, +0.00279659017898,
            +0.01083740322821, -0.06449599336038, +0.01794514261221, +0.01036809141699, +0.04283504450449,
            +0.00634472273784, +0.02339134834111, -0.01748667848380, +0.00161859106290, +0.00622486432503,
            -0.05854130195643, +0.15083728660504, +0.00030733757661, -0.00143739522173, -0.05295810171941 },
            { -0.14637948915627, +0.02029296323583, +0.02615316895036, -0.10311538564943, -0.00183412744544,
            -0.02589124656591, +0.11073673851935, +0.00848581728407, +0.00106057791901, +0.05530240732939,
            -0.00031533506946, -0.03124002869407, -0.01533984125301, -0.00288717337278, +0.00272787410643,
            +0.06300929916280, +0.07920438311152, -0.00041335282410, -0.00011648873397, -0.03944076085434 },
            { -0.05558229086909, +0.08935293782491, +0.04869509588770, +0.04856877988810, -0.00253836047720,
            +0.07651693957635, -0.06342453535092, -0.00777376246014, -0.08570270266807, +0.01943016473512,
            -0.00599516526932, -0.09157595008575, -0.00397735155663, -0.00440093863690, -0.00232998056918,
            +0.02979967701162, -0.00477299485901, -0.00144011795333, +0.01795114942404, -0.00080059359232 },
            { +0.05807741644682, +0.14654292420341, -0.06724975334073, +0.02159062346633, -0.00339085518294,
            -0.06829036785575, +0.03520631903157, -0.02766062718318, +0.03485632707432, -0.02436836692465,
            -0.00397566003573, -0.10095488644404, +0.02456887654357, +0.00381764117077, -0.00906261340247,
            -0.01043058066362, +0.01651199513994, -0.00210417220821, -0.00872508520963, -0.01495915462580 },
            { +0.02564617106907, +0.02960554611436, -0.00052356748770, +0.00989267817318, -0.00044034172141,
            -0.02279910634723, -0.00363768356471, -0.01086345665971, +0.01229721799572, +0.02633650142592,
            +0.06282966783922, -0.00734486499924, -0.13863936313277, -0.00993891943390, -0.00655309682350,
            -0.00245191788287, -0.02431633805559, -0.00068554031525, -0.00121383858869, +0.06280025239509 },
            { +0.11362428251792, -0.02080375718488, -0.08802750967213, -0.06531316372189, -0.00166626058292,
            +0.06846081717224, +0.07007301248407, -0.01713112936632, -0.05900588794853, -0.04497159138485,
            +0.04222484636983, +0.00129043178508, -0.01550337251561, -0.01553102163852, -0.04363429852047,
            +0.01600063777880, +0.05787328925647, -0.00008265841118, +0.02870014572813, -0.02657681214523 },
            { +0.01840541226842, +0.00610159018805, +0.01368080422265, +0.02383751807012, -0.00923516894192,
            +0.01209943150832, +0.02906782189141, +0.01992384905334, +0.00197323568330, +0.00017531415423,
            -0.01796698381949, +0.01887083962858, -0.00063335886734, -0.02365277334702, +0.01209445088200,
            +0.01308086447947, +0.01286727242301, -0.11420358975688, -0.01886991700613, +0.00238338728588 },
            { -0.01100105031759, -0.04250695864938, -0.02554356700969, -0.05473632078607, +0.00725906469946,
            -0.03003724918191, -0.07051526125013, -0.06939439879112, -0.00285883056088, +0.05334304124753,
            +0.12839241846919, -0.05883473754222, +0.02424304967487, +0.09134510778469, -0.00226003347193,
            -0.01280041778462, -0.00207988305627, -0.02957493909199, +0.05290385686789, +0.05465710875015 },
            { -0.01421274522011, +0.02074863337778, -0.01006411985628, +0.03319995456446, -0.00005371699269,
            -0.12266046460835, +0.02419847062899, -0.00441168706583, -0.08299118738167, -0.00323230913482,
            +0.02954035119881, +0.09212856795583, +0.00718635627257, -0.02706936115539, +0.04473173279913,
            -0.01274357634785, -0.01395862740618, -0.00071538848681, +0.04767640012830, -0.00729728326990 },
            { -0.03797680968123, +0.01280286509478, -0.08614616553187, -0.01781049963160, +0.00674319990083,
            +0.04208667754694, +0.05991325707583, +0.03581015660092, -0.01529816709967, +0.06885987924922,
            -0.11719120476535, -0.00014333663810, +0.00074336784254, +0.02893416406249, +0.07466151360134,
            -0.08182016471377, -0.06581536577662, -0.00018195976501, +0.00167443595008, +0.09015415667825 },
            { +0.03577726799591, -0.02139253448219, -0.01137813538175, -0.01954939202830, -0.04028242801611,
            -0.01777500032351, -0.02106862264440, +0.00465199658293, -0.02824805812709, +0.06618860061778,
            +0.08437791757537, -0.02533125946051, +0.02806344654855, -0.06970805797879, +0.02328376968627,
            +0.00692992333282, +0.02751392122018, +0.01148722812804, -0.11130404325078, +0.07776346000559 },
            { -0.06014297925310, -0.00711674355952, -0.02424493472566, +0.00032464353156, +0.00321221847573,
            +0.03257969053884, +0.01072805771161, +0.06892027923996, +0.03326534127710, -0.01558838623875,
            +0.13794237677194, -0.04292623056646, +0.01375763233229, -0.11125153774789, +0.03510076081639,
            -0.04531670712549, -0.06170413486351, -0.00182023682123, +0.05979891871679, -0.02551802851059 },
            { -0.03515069991501, +0.02310847227710, +0.00474493548551, +0.02787717003457, -0.12038329679812,
            +0.03178473522077, +0.04445111601130, -0.05334957493090, +0.01290386678474, -0.00376064171612,
            +0.03996642737967, +0.04777677295520, +0.00233689200639, +0.03917715404594, -0.01755598277531,
            -0.03389088626433, -0.02180780263389, +0.00473402043911, +0.01964539477020, -0.01260807237680 },
            { -0.04120428254254, +0.00062717164978, -0.01688703578637, +0.01685776910152, +0.02102702093943,
            +0.01295781834163, +0.03541815979495, +0.03968150445315, -0.02073122710938, -0.06932247350110,
            +0.11696314241296, -0.00322523765776, -0.01280515661402, +0.08717664266126, +0.06297225078802,
            -0.01290501780488, -0.04693925076877, -0.00177653675449, -0.08407812137852, -0.08380714022487 },
            { +0.03138655228534, -0.09052573757196, +0.00874202219428, +0.06060593729292, -0.03426076652151,
            -0.04832468257386, +0.04735628794421, +0.14504653737383, -0.01709111334001, -0.00278794215381,
            -0.03513813820550, -0.11690294831883, -0.00836264902624, +0.03270980973180, -0.02587764129811,
            +0.01638786059073, +0.00485499822497, +0.00305477087025, +0.02295754527195, +0.00616929722958 },
            { -0.04898722042023, -0.01460879656586, +0.00508708857036, +0.07730497806331, +0.04252420017435,
            +0.00484232580349, +0.09861807969412, -0.05169447907187, -0.00917820907880, +0.03679081047330,
            +0.04998537112655, +0.00769330211980, +0.01805447683564, -0.00498723245027, -0.14148416183376,
            -0.05170281760262, -0.03230723310784, -0.00032890672639, -0.02363523071957, +0.03801365471627 },
            { -0.02047562162108, +0.06933781779590, -0.02101117884731, -0.06841945874842, -0.00860967572716,
            -0.00886650271590, -0.07185241332269, +0.16703684361030, -0.00635847581692, +0.00811478913823,
            +0.01847205842216, +0.06700967948643, +0.00596607376199, +0.02318239240593, -0.10552958537847,
            -0.01980199747773, -0.02003785382406, -0.00593392430159, -0.00965391033612, +0.00743094349652 } };
    // PMB matrix decomposition courtesy of Elisabeth Tillier 
    final private static double pmbeigs[]    = { 0.0000001586972220, -1.8416770496147100, -1.6025046986139100,
            -1.5801012515121300, -1.4987794099715900, -1.3520794233801900, -1.3003469390479700, -1.2439503327631300,
            -1.1962574080244200, -1.1383730501367500, -1.1153278910708000, -0.4934843510654760, -0.5419014550215590,
            -0.9657997830826700, -0.6276075673757390, -0.6675927795018510, -0.6932641383465870, -0.8897872681859630,
            -0.8382698977371710, -0.8074694642446040 };
    final private static double pmbprobs[][] = {
            { 0.0771762457248147, 0.0531913844998640, 0.0393445076407294, 0.0466756566755510, 0.0286348361997465,
            0.0312327748383639, 0.0505410248721427, 0.0767106611472993, 0.0258916271688597, 0.0673140562194124,
            0.0965705469252199, 0.0515979465932174, 0.0250628079438675, 0.0503492018628350, 0.0399908189418273,
            0.0641898881894471, 0.0517539616710987, 0.0143507440546115, 0.0357994592438322, 0.0736218495862984 },
            { 0.0368263046116572, -0.0006728917107827, 0.0008590805287740, -0.0002764255356960, 0.0020152937187455,
            0.0055743720652960, 0.0003213317669367, 0.0000449190281568, -0.0004226254397134, 0.1805040629634510,
            -0.0272246813586204, 0.0005904606533477, -0.0183743200073889, -0.0009194625608688, 0.0008173657533167,
            -0.0262629806302238, 0.0265738757209787, 0.0002176606241904, 0.0021315644838566, -0.1823229927207580 },
            { -0.0194800075560895, 0.0012068088610652, -0.0008803318319596, -0.0016044273960017, -0.0002938633803197,
            -0.0535796754602196, 0.0155163896648621, -0.0015006360762140, 0.0021601372013703, 0.0268513218744797,
            -0.1085292493742730, 0.0149753083138452, 0.1346457366717310, -0.0009371698759829, 0.0013501708044116,
            0.0346352293103622, -0.0276963770242276, 0.0003643142783940, 0.0002074817333067, -0.0174108903914110 },
            { 0.0557839400850153, 0.0023271577185437, 0.0183481103396687, 0.0023339480096311, 0.0002013267015151,
            -0.0227406863569852, 0.0098644845475047, 0.0064721276774396, 0.0001389408104210, -0.0473713878768274,
            -0.0086984445005797, 0.0026913674934634, 0.0283724052562196, 0.0001063665179457, 0.0027442574779383,
            -0.1875312134708470, 0.1279864877057640, 0.0005103347834563, 0.0003155113168637, 0.0081451082759554 },
            { 0.0037510125027265, 0.0107095920636885, 0.0147305410328404, -0.0112351252180332, -0.0001500408626446,
            -0.1523450933729730, 0.0611532413339872, -0.0005496748939503, 0.0048714378736644, -0.0003826320053999,
            0.0552010244407311, 0.0482555671001955, -0.0461664995115847, -0.0021165008617978, -0.0004574454232187,
            0.0233755883688949, -0.0035484915422384, 0.0009090698422851, 0.0013840637687758, -0.0073895139302231 },
            { -0.0111512564930024, 0.1025460064723080, 0.0396772456883791, -0.0298408501361294, -0.0001656742634733,
            -0.0079876311843289, 0.0712644184507945, -0.0010780604625230, -0.0035880882043592, 0.0021070399334252,
            0.0016716329894279, -0.1810123023850110, 0.0015141703608724, -0.0032700852781804, 0.0035503782441679,
            0.0118634302028026, 0.0044561606458028, -0.0001576678495964, 0.0023470722225751, -0.0027457045397157 },
            { 0.1474525743949170, -0.0054432538500293, 0.0853848892349828, -0.0137787746207348, -0.0008274830358513,
            0.0042248844582553, 0.0019556229305563, -0.0164191435175148, -0.0024501858854849, 0.0120908948084233,
            -0.0381456105972653, 0.0101271614855119, -0.0061945941321859, 0.0178841099895867, -0.0014577779202600,
            -0.0752120602555032, -0.1426985695849920, 0.0002862275078983, -0.0081191734261838, 0.0313401149422531 },
            { 0.0542034611735289, -0.0078763926211829, 0.0060433542506096, 0.0033396210615510, 0.0013965072374079,
            0.0067798903832256, -0.0135291136622509, -0.0089982442731848, -0.0056744537593887, -0.0766524225176246,
            0.1881210263933930, -0.0065875518675173, 0.0416627569300375, -0.0953804133524747, -0.0012559228448735,
            0.0101622644292547, -0.0304742453119050, 0.0011702318499737, 0.0454733434783982, -0.1119239362388150 },
            { 0.1069409037912470, 0.0805064400880297, -0.1127352030714600, 0.1001181253523260, -0.0021480427488769,
            -0.0332884841459003, -0.0679837575848452, -0.0043812841356657, 0.0153418716846395, -0.0079441315103188,
            -0.0121766182046363, -0.0381127991037620, -0.0036338726532673, 0.0195324059593791, -0.0020165963699984,
            -0.0061222685010268, -0.0253761448771437, -0.0005246410999057, -0.0112205170502433, 0.0052248485517237 },
            { -0.0325247648326262, 0.0238753651653669, 0.0203684886605797, 0.0295666232678825, -0.0003946714764213,
            -0.0157242718469554, -0.0511737848084862, 0.0084725632040180, -0.0167068828528921, 0.0686962159427527,
            -0.0659702890616198, -0.0014289912494271, -0.0167000964093416, -0.1276689083678200, 0.0036575057830967,
            -0.0205958145531018, 0.0000368919612829, 0.0014413626622426, 0.1064360941926030, 0.0863372661517408 },
            { -0.0463777468104402, 0.0394712148670596, 0.1118686750747160, 0.0440711686389031, -0.0026076286506751,
            -0.0268454015202516, -0.1464943067133240, -0.0137514051835380, -0.0094395514284145, -0.0144124844774228,
            0.0249103379323744, -0.0071832157138676, 0.0035592787728526, 0.0415627419826693, 0.0027040097365669,
            0.0337523666612066, 0.0316121324137152, -0.0011350177559026, -0.0349998884574440, -0.0302651879823361 },
            { 0.0142360925194728, 0.0413145623127025, 0.0324976427846929, 0.0580930922002398, -0.0586974207121084,
            0.0202001168873069, 0.0492204086749069, 0.1126593173463060, 0.0116620013776662, -0.0780333711712066,
            -0.1109786767320410, 0.0407775100936731, -0.0205013161312652, -0.0653458585025237, 0.0347351829703865,
            0.0304448983224773, 0.0068813748197884, -0.0189002309261882, -0.0334507528405279, -0.0668143558699485 },
            { -0.0131548829657936, 0.0044244322828034, -0.0050639951827271, -0.0038668197633889, -0.1536822386530220,
            0.0026336969165336, 0.0021585651200470, -0.0459233839062969, 0.0046854727140565, 0.0393815434593599,
            0.0619554007991097, 0.0027456299925622, 0.0117574347936383, 0.0373018612990383, 0.0024818527553328,
            -0.0133956606027299, -0.0020457128424105, 0.0154178819990401, 0.0246524142683911, 0.0275363065682921 },
            { -0.1542307272455030, 0.0364861558267547, -0.0090880407008181, 0.0531673937889863, 0.0157585615170580,
            0.0029986538457297, 0.0180194047699875, 0.0652152443589317, 0.0266842840376180, 0.0388457366405908,
            0.0856237634510719, 0.0126955778952183, 0.0099593861698250, -0.0013941794862563, 0.0294065511237513,
            -0.1151906949298290, -0.0852991447389655, 0.0028699120202636, -0.0332087026659522, 0.0006811857297899 },
            { 0.0281300736924501, -0.0584072081898638, -0.0178386569847853, -0.0536470338171487, -0.0186881656029960,
            -0.0240008730656106, -0.0541064820498883, 0.2217137098936020, -0.0260500001542033, 0.0234505236798375,
            0.0311127151218573, -0.0494139126682672, 0.0057093465049849, 0.0124937286655911, -0.0298322975915689,
            0.0006520211333102, -0.0061018680727128, -0.0007081999479528, -0.0060523759094034, 0.0215845995364623 },
            { 0.0295321046399105, -0.0088296411830544, -0.0065057049917325, -0.0053478115612781, -0.0100646496794634,
            -0.0015473619084872, 0.0008539960632865, -0.0376381933046211, -0.0328135588935604, 0.0672161874239480,
            0.0667626853916552, -0.0026511651464901, 0.0140451514222062, -0.0544836996133137, 0.0427485157912094,
            0.0097455780205802, 0.0177309072915667, -0.0828759701187452, -0.0729504795471370, 0.0670731961252313 },
            { 0.0082646581043963, -0.0319918630534466, -0.0188454445200422, -0.0374976353856606, 0.0037131290686848,
            -0.0132507796987883, -0.0306958830735725, -0.0044119395527308, -0.0140786756619672, -0.0180512599925078,
            -0.0208243802903953, -0.0232202769398931, -0.0063135878270273, 0.0110442171178168, 0.1824538048228460,
            -0.0006644614422758, -0.0069909097436659, 0.0255407650654681, 0.0099119399501151, -0.0140911517070698 },
            { 0.0261344441524861, -0.0714454044548650, 0.0159436926233439, 0.0028462736216688, -0.0044572637889080,
            -0.0089474834434532, -0.0177570282144517, -0.0153693244094452, 0.1160919467206400, 0.0304911481385036,
            0.0047047513411774, -0.0456535116423972, 0.0004491494948617, -0.0767108879444462, -0.0012688533741441,
            0.0192445965934123, 0.0202321954782039, 0.0281039933233607, -0.0590403018490048, 0.0364080426546883 },
            { 0.0115826306265004, 0.1340228176509380, -0.0236200652949049, -0.1284484655137340, -0.0004742338006503,
            0.0127617346949511, -0.0428560878860394, 0.0060030732454125, 0.0089182609926781, 0.0085353834972860,
            0.0048464809638033, 0.0709740071429510, 0.0029940462557054, -0.0483434904493132, -0.0071713680727884,
            -0.0036840391887209, 0.0031454003250096, 0.0246243550241551, -0.0449551277644180, 0.0111449232769393 },
            { 0.0140356721886765, -0.0196518236826680, 0.0030517022326582, 0.0582672093364850, -0.0000973895685457,
            0.0021704767224292, 0.0341806268602705, -0.0152035987563018, -0.0903198657739177, 0.0259623214586925,
            0.0155832497882743, -0.0040543568451651, 0.0036477631918247, -0.0532892744763217, -0.0142569373662724,
            0.0104500681408622, 0.0103483945857315, 0.0679534422398752, -0.0768068882938636, 0.0280289727046158 } };
    // dcmut version of PAM model from http://www.ebi.ac.uk/goldman-srv/dayhoff/ 
    final private static double pameigs[]    = { 0, -1.93321786301018, -2.20904642493621, -1.74835983874903,
            -1.64854548332072, -1.54505559488222, -1.33859384676989, -1.29786201193594, -0.235548517495575,
            -0.266951066089808, -0.28965813670665, -1.10505826965282, -1.04323310568532, -0.430423720979904,
            -0.541719761016713, -0.879636093986914, -0.711249353378695, -0.725050487280602, -0.776855937389452,
            -0.808735559461343              };
    final private static double pamprobs[][] = {
            { 0.08712695644, 0.04090397955, 0.04043197978, 0.04687197656, 0.03347398326, 0.03825498087, 0.04952997524,
            0.08861195569, 0.03361898319, 0.03688598156, 0.08535695732, 0.08048095976, 0.01475299262, 0.03977198011,
            0.05067997466, 0.06957696521, 0.05854197073, 0.01049399475, 0.02991598504, 0.06471796764 },
            { 0.07991048383, 0.006888314018, 0.03857806206, 0.07947073194, 0.004895492884, 0.03815829405,
            -0.1087562465, 0.008691167141, -0.0140554828, 0.001306404001, -0.001888411299, -0.006921303342,
            0.0007655604228, 0.001583298443, 0.006879590446, -0.171806883, 0.04890917949, 0.0006700432804,
            0.0002276237277, -0.01350591875 },
            { -0.01641514483, -0.007233933239, -0.1377830621, 0.1163201333, -0.002305138017, 0.01557250366,
            -0.07455879489, -0.003225343503, 0.0140630487, 0.005112274204, 0.001405731862, 0.01975833782,
            -0.001348402973, -0.001085733262, -0.003880514478, 0.0851493313, -0.01163526615, -0.0001197903399,
            0.002056153393, 0.0001536095643 },
            { 0.009669278686, -0.006905863869, 0.101083544, 0.01179903104, -0.003780967591, 0.05845105878,
            -0.09138357299, -0.02850503638, -0.03233951408, 0.008708065876, -0.004700705411, -0.02053221579,
            0.001165851398, -0.001366585849, -0.01317695074, 0.1199985703, -0.1146346193, -0.0005953021314,
            -0.0004297615194, 0.007475695618 },
            { 0.1722243502, -0.003737582995, -0.02964873222, -0.02050116381, -0.0004530478465, -0.02460043205,
            0.02280768412, -0.02127364909, 0.01570095258, 0.1027744285, -0.005330539586, 0.0179697651, -0.002904077286,
            -0.007068126663, -0.0142869583, -0.01444241844, -0.08218861544, 0.0002069181629, 0.001099671379,
            -0.1063484263 },
            { -0.1553433627, -0.001169168032, 0.02134785337, 0.0007602305436, 0.0001395330122, 0.03194992019,
            -0.01290252206, 0.03281720789, -0.01311103735, 0.1177254769, -0.008008783885, -0.02375317548,
            -0.002817809762, -0.008196682776, 0.01731267617, 0.01853526375, 0.08249908546, -2.788771776e-05,
            0.001266182191, -0.09902299976 },
            { -0.03671080341, 0.0274168035, 0.04625877597, 0.07520706414, -0.0001833803619, -0.1207833161,
            -0.006415807779, -0.005465629648, 0.02778273972, 0.007589688485, -0.02945266034, -0.03797542064,
            0.07044042052, -0.002018573865, 0.01845277071, 0.006901513991, -0.02430934639, -0.0005919635873,
            -0.001266962331, -0.01487591261 },
            { -0.03060317816, 0.01182361623, 0.04200270053, 0.05406235279, -0.0003920498815, -0.09159709348,
            -0.009602690652, -0.00382944418, 0.01761361993, 0.01605684317, 0.05198878008, 0.02198696949,
            -0.09308930025, -0.00102622863, 0.01477637127, 0.0009314065393, -0.01860959472, -0.0005964703968,
            -0.002694284083, 0.02079767439 },
            { 0.0195976494, -0.005104484936, 0.007406728707, 0.01236244954, 0.0201446796, 0.007039564785,
            0.01276942134, 0.02641595685, 0.002764624354, 0.001273314658, -0.01335316035, 0.01105658671,
            2.148773499e-05, -0.02692205639, 0.0118684991, 0.01212624708, 0.01127770094, -0.09842754796,
            -0.01942336432, 0.007105703151 },
            { -0.01819461888, -0.01509348507, -0.01297636935, -0.01996453439, 0.1715705905, -0.01601550692,
            -0.02122706144, -0.02854628494, -0.009351082371, -0.001527995472, -0.010198224, -0.03609537551,
            -0.003153182095, 0.02395980501, -0.01378664626, -0.005992611421, -0.01176810875, 0.003132361603,
            0.03018439539, -0.004956065656 },
            { -0.02733614784, -0.02258066705, -0.0153112506, -0.02475728664, -0.04480525045, -0.01526640341,
            -0.02438517425, -0.04836914601, -0.00635964824, 0.02263169831, 0.09794101931, -0.04004304158,
            0.008464393478, 0.1185443142, -0.02239294163, -0.0281550321, -0.01453581604, -0.0246742804, 0.0879619849,
            0.02342867605 },
            { 0.06483718238, 0.1260012082, -0.006496013283, 0.009914915531, -0.004181603532, 0.0003493226286,
            0.01408035752, -0.04881663016, -0.03431167356, -0.01768005602, 0.02362447761, -0.1482364784,
            -0.01289035619, -0.001778893279, -0.05240099752, 0.05536174567, 0.06782165352, -0.003548568717,
            0.001125301173, -0.03277489363 },
            { 0.06520296909, -0.0754802543, 0.03139281903, -0.03266449554, -0.004485188002, -0.03389072036,
            -0.06163274338, -0.06484769882, 0.05722658289, -0.02824079619, 0.01544837349, 0.03909752708,
            0.002029218884, 0.003151939572, -0.05471208363, 0.07962008342, 0.125916047, 0.0008696184937,
            -0.01086027514, -0.05314092355 },
            { 0.004543119081, 0.01935177735, 0.01905511007, 0.02682993409, -0.01199617967, 0.01426278655,
            0.02472521255, 0.03864795501, 0.02166224804, -0.04754243479, -0.1921545477, 0.03621321546, -0.02120627881,
            0.04928097895, 0.009396088815, 0.01748042052, -6.173742851e-05, -0.003168033098, 0.07723565812,
            -0.08255529309 },
            { 0.06710378668, -0.09441410284, -0.004801776989, 0.008830272165, -0.01021645042, -0.02764365608,
            0.004250361851, 0.1648777542, -0.037446109, 0.004541057635, -0.0296980702, -0.1532325189, -0.008940580901,
            0.006998050812, 0.02338809379, 0.03175059182, 0.02033965512, 0.006388075608, 0.001762762044, 0.02616280361 },
            { 0.01915943021, -0.05432967274, 0.01249342683, 0.06836622457, 0.002054462161, -0.01233535859,
            0.07087282652, -0.08948637051, -0.1245896013, -0.02204522882, 0.03791481736, 0.06557467874, 0.005529294156,
            -0.006296644235, 0.02144530752, 0.01664230081, 0.02647078439, 0.001737725271, 0.01414149877, -0.05331990116 },
            { 0.0266659303, 0.0564142853, -0.0263767738, -0.08029726006, -0.006059357163, -0.06317558457,
            -0.0911894019, 0.05401487057, -0.08178072458, 0.01580699778, -0.05370550396, 0.09798653264, 0.003934944022,
            0.01977291947, 0.0441198541, 0.02788220393, 0.03201877081, -0.00206161759, -0.005101423308, 0.03113033802 },
            { 0.02980360751, -0.009513246268, -0.009543527165, -0.02190644172, -0.006146440672, 0.01207009085,
            -0.0126989156, -0.1378266418, 0.0275235217, 0.00551720592, -0.03104791544, -0.07111701247, -0.006081754489,
            -0.01337494521, 0.1783961085, 0.01453225059, 0.01938736048, 0.0004488631071, 0.0110844398, 0.02049339243 },
            { -0.01433508581, 0.01258858175, -0.004294252236, -0.007146532854, 0.009541628809, 0.008040155729,
            -0.006857781832, 0.05584120066, 0.007749418365, -0.05867835844, 0.08008131283, -0.004877854222,
            -0.0007128540743, 0.09489058424, 0.06421121962, 0.00271493526, -0.03229944773, -0.001732026038,
            -0.08053448316, -0.1241903609 },
            { -0.009854113227, 0.01294129929, -0.00593064392, -0.03016833115, -0.002018439732, -0.00792418722,
            -0.03372768732, 0.07828561288, 0.007722254639, -0.05067377561, 0.1191848621, 0.005059475202,
            0.004762387166, -0.1029870175, 0.03537190114, 0.001089956203, -0.02139157573, -0.001015245062,
            0.08400521847, -0.08273195059 } };
}
