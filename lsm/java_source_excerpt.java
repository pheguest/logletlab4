CODE excerpt from: public class LsmDataModel extends AbstractTableModel(SVN rev 4017 - starting at line 493)

 public Collection<LsmCurveModel> getLsmDsc(boolean useFisherPry) {
        double dataMin = Double.MAX_VALUE;
        Collection<LsmCurveModel> modelDsc = new LinkedList<LsmCurveModel>();
        int setSize = getSeriesList().size();
        double alpha[] = new double[setSize];
        double beta[] = new double[setSize];
        double[] alpha1 = new double[setSize];
        double[] beta1 = new double[setSize];
        double[] alpha2 = new double[setSize];
        double[] beta2 = new double[setSize];
        double[] t1 = new double[setSize];
        double[] t2 = new double[setSize];
        // the public parameters
        Alpha1 = new Double[setSize];
        Beta1 = new Double[setSize];
        Alpha2 = new Double[setSize];
        Beta2 = new Double[setSize];
        T1 = new Double[setSize];
        T2 = new Double[setSize];
        int iset = 0;
        for (Iterator<?> it = getSeriesList().iterator(); it.hasNext();) {
            String aName = (String)it.next();
            TreeMap<?, ?> ydata = (TreeMap<?, ?>)getYData(aName);
            if (ydata.size() > 0) {
                if (((Double)ydata.firstKey()).doubleValue() < dataMin) {
                    dataMin = ((Double)ydata.firstKey()).doubleValue();
                }
            }
            FitCurve fc = getLsmParameter(aName);
            if (fc != null) {
                alpha1[iset] = fc.getBeta().doubleValue();
                beta1[iset] = fc.getAlpha().doubleValue();
                alpha[iset] = fc.getBeta().doubleValue();
                beta[iset] = fc.getAlpha().doubleValue();
            }
            iset++;
        }
        //
        // find saturation levels
        int XSTART = getDispMin().intValue();
        int XDONE = getDispMax().intValue();
        double dIncr = getDispIncrement().doubleValue();
        alpha2[0] = alpha[0];
        beta2[0] = beta[0];
        Alpha2[0] = new Double(Math.log(81)/alpha2[0]);
        Beta2[0] = new Double(beta2[0]/alpha2[0]*-1);
        t1[0] = t2[0] = t1[1] = XSTART;
        int ntech = iset;
        int itech = 1;
        double qold = 1.e+30;
        double quot, fj, yt, ytp, ytpp;
        HashMap<Double, Double> sData = new HashMap<Double, Double>();
        double iy = 0;
        for (iy=XSTART; iy<=XDONE; iy+=dIncr) {
            double x = (double)iy;
            double derivs[] = Derivs(x, ntech, itech, alpha, beta);
            yt = derivs[0];
            ytp = derivs[1];
            ytpp = derivs[2];
            fj = 1./(1.+Math.exp(-yt));   // market share
            //logger.info(x+" "+fj+" "+yt+" "+ytp+" "+ytpp+" "+ytpp/ytp);
            if (x>=dataMin) {
                if (ytp >= 0.) {
                    sData.put(new Double(x), new Double(fj));
                } else {
                    quot = ytpp/ytp;
                    //logger.info("quot:"+qold+" "+quot);
                    if (quot < qold) {
                        qold = quot;
                        sData.put(new Double(x), new Double(fj));
                    } else { //  ! minimal curvature reached
                        alpha2[itech] = ytp;
                        beta2[itech] = yt-ytp*x;
                        Alpha2[itech] = new Double(Math.log(81)/alpha2[itech]);
                        Beta2[itech] = new Double(beta2[itech]/alpha2[itech]*-1);
                        alpha[itech] = alpha2[itech];
                        beta[itech] = beta2[itech];
                        //logger.info("itech1: "+itech+" "+x);
                        t2[itech] = x;
                        T2[itech] = new Double(x);
                        itech++;
                        if (itech==ntech) break;
                        Alpha1[itech] = new Double(Math.log(81)/alpha1[itech]);
                        Beta1[itech] = new Double(beta1[itech]/alpha1[itech]*-1);
                        t1[itech] = x;
                        T1[itech] = new Double(x);
                        qold  = 1.e+30;
                    }
                }
            } else {
                if (ytp >= 0.) {
                    sData.put(new Double(x), new Double(fj));
                }
            }
        }
        if (itech<ntech) {
            t2[itech] = iy;
        }
        logger.debug("itech2: "+itech+" "+iy);
        for (int i=itech ; i<ntech; i++) {
            t1[i] = t2[i-1];
            if (t1[i] == 0) {
                t1[i] = t1[i-1];
            }
            T1[i] = new Double(t1[i]);
            Alpha1[i] = new Double(Math.log(81)/alpha1[i]);
            Beta1[i] = new Double(beta1[i]/alpha1[i]*-1);
        }
        //
        for (int i=0; i<ntech; i++){
            logger.debug("itech: "+i+" "+t1[i]+" "+t2[i]+" "+alpha2[i]);
        }
        //
        iset = 0;
        for (Iterator<?> it1 = getSeriesList().iterator(); it1.hasNext(); ) {
            String aName = (String)it1.next();
            LsmCurveModel modelSet = new LsmCurveModel(aName);
            for (double d=XSTART; d<=XDONE; d=d+dIncr) {
                if (d < t1[iset]) {
                    double y = 1./(1.+Math.exp(-alpha1[iset]*d-beta1[iset]));
                    if (useFisherPry) {
                        y = y / (1 - y);
                    }
                    modelSet.addPoint(d, y, true);
                } else if (d < t2[iset]) {
                    try {
                        double y = ((Double)sData.get(new Double(d))).doubleValue();
                        if (useFisherPry) {
                            y = y / (1 - y);
                        }
                        modelSet.addPoint(d, y, true);
                    } catch (NullPointerException e) {
                        ;
                    } catch (Exception e) {
                        logger.error("1", e);
                    }
                } else {
                    double y = 1./(1.+Math.exp(-alpha2[iset]*d-beta2[iset]));
                    if (useFisherPry) {
                        y = y / (1 - y);
                    }
                    modelSet.addPoint(d, y, true);                  
                }
            }
            iset++;
            modelDsc.add(modelSet);
        }
        return modelDsc;
    }

    double[] Derivs( double t, int ntech, int itech, double al[], double be[]) {
        double fj = 1.0;
        double fjp = 0.0;
        double fjpp = 0.0;
        
        double arg = 0;
        double ex =0;
        double exi = 0;
        double term = 0;
        for (int k=0; k<ntech; k++) {
            if (k != itech) {
                arg = al[k]*t+be[k];
                if (arg < -30.0) arg = -30.0;
                ex = Math.exp(-arg);
                exi = 1.0/(1.+ex);
                fj = fj-exi;
                term = al[k]*ex*exi*exi;
                fjp = fjp-term;
                fjpp = fjpp+term*al[k]*(1.0-ex)*exi;
            }
        }
        if (fj < 1.e-8) fj = 1.e-8;
        if (fj > (1.0-1.e-8)) fj = 1.0-1.e-8;

        double fji = 1.0/fj;
        double fji1 = 1.0/(1.0-fj);
        double yt = Math.log(fj*fji1);
        double ytp = (fji+fji1)*fjp;
        double ytpp = (fji+fji1)*(fjpp-fjp*fjp*(fji-fji1));
        double retval[] = new double[3];
        retval[0] = yt;
        retval[1] = ytp;
        retval[2] = ytpp;
        return retval;
    }
