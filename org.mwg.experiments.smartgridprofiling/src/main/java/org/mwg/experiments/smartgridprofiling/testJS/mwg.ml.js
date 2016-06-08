var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var org;
(function (org) {
    var mwg;
    (function (mwg) {
        var ml;
        (function (ml) {
            var common;
            (function (common) {
                var matrix;
                (function (matrix) {
                    var blassolver;
                    (function (blassolver) {
                        var blas;
                        (function (blas) {
                            var JSBlas = (function () {
                                function JSBlas() {
                                    this.netlib = Module;
                                }
                                JSBlas.prototype.dgemm = function (transA, transB, m, n, k, alpha, matA, offsetA, ldA, matB, offsetB, ldB, beta, matC, offsetC, ldC) {
                                    var ptransA = this.netlib._malloc(1), ptransB = this.netlib._malloc(1), pm = this.netlib._malloc(4), pn = this.netlib._malloc(4), pk = this.netlib._malloc(4), palpha = this.netlib._malloc(8), pmatA = this.netlib._malloc(8 * matA.length), pldA = this.netlib._malloc(4), pmatB = this.netlib._malloc(8 * matB.length), pldB = this.netlib._malloc(4), pbeta = this.netlib._malloc(8), pmatC = this.netlib._malloc(8 * matC.length), pldC = this.netlib._malloc(4);
                                    this.netlib.setValue(ptransA, org.mwg.ml.common.matrix.blassolver.blas.BlasHelper.transTypeToChar(transA).charCodeAt(0), 'i8');
                                    this.netlib.setValue(ptransB, org.mwg.ml.common.matrix.blassolver.blas.BlasHelper.transTypeToChar(transB).charCodeAt(0), 'i8');
                                    this.netlib.setValue(pm, m, 'i32');
                                    this.netlib.setValue(pn, n, 'i32');
                                    this.netlib.setValue(pk, k, 'i32');
                                    this.netlib.setValue(palpha, alpha, 'double');
                                    var ddpmatA = new Float64Array(this.netlib.HEAPF64.buffer, pmatA, matA.length);
                                    ddpmatA.set(matA);
                                    this.netlib.setValue(pldA, ldA, 'i32');
                                    var ddpmatB = new Float64Array(this.netlib.HEAPF64.buffer, pmatB, matB.length);
                                    ddpmatB.set(matB);
                                    this.netlib.setValue(pldB, ldB, 'i32');
                                    this.netlib.setValue(pbeta, beta, 'double');
                                    var ddpmatC = new Float64Array(this.netlib.HEAPF64.buffer, pmatC, matC.length);
                                    ddpmatC.set(matC);
                                    this.netlib.setValue(pldC, ldC, 'i32');
                                    var dgemm = this.netlib.cwrap('f2c_dgemm', null, ['number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number']);
                                    dgemm(ptransA, ptransB, pm, pn, pk, palpha, pmatA, pldA, pmatB, pldB, pbeta, pmatC, pldC);
                                    // matA.set(ddpmatA);
                                    // matB.set(ddpmatB);
                                    matC.set(ddpmatC);
                                    this.netlib._free(ptransA);
                                    this.netlib._free(ptransB);
                                    this.netlib._free(pm);
                                    this.netlib._free(pn);
                                    this.netlib._free(pk);
                                    this.netlib._free(palpha);
                                    this.netlib._free(pmatA);
                                    this.netlib._free(pldA);
                                    this.netlib._free(pmatB);
                                    this.netlib._free(pldB);
                                    this.netlib._free(pbeta);
                                    this.netlib._free(pmatC);
                                    this.netlib._free(pldC);
                                };
                                JSBlas.prototype.dgetrs = function (transA, dim, nrhs, matA, offsetA, ldA, ipiv, offsetIpiv, matB, offsetB, ldB, info) {
                                    var ptransA = this.netlib._malloc(1), pdim = this.netlib._malloc(4), pnrhs = this.netlib._malloc(4), pmatA = this.netlib._malloc(8 * matA.length), pldA = this.netlib._malloc(4), pipiv = this.netlib._malloc(4 * ipiv.length), pmatB = this.netlib._malloc(8 * matB.length), pldB = this.netlib._malloc(4), pinfo = this.netlib._malloc(4 * info.length);
                                    this.netlib.setValue(ptransA, org.mwg.ml.common.matrix.blassolver.blas.BlasHelper.transTypeToChar(transA).charCodeAt(0), 'i8');
                                    this.netlib.setValue(pdim, dim, 'i32');
                                    this.netlib.setValue(pnrhs, nrhs, 'i32');
                                    var ddpmatA = new Float64Array(this.netlib.HEAPF64.buffer, pmatA, matA.length);
                                    ddpmatA.set(matA);
                                    this.netlib.setValue(pldA, ldA, 'i32');
                                    var iipipiv = new Int32Array(this.netlib.HEAP32.buffer, pipiv, ipiv.length);
                                    iipipiv.set(ipiv);
                                    var ddpmatB = new Float64Array(this.netlib.HEAPF64.buffer, pmatB, matB.length);
                                    ddpmatB.set(matB);
                                    this.netlib.setValue(pldB, ldB, 'i32');
                                    var iipinfo = new Int32Array(this.netlib.HEAP32.buffer, pinfo, info.length);
                                    iipinfo.set(info);
                                    var dgetrs = this.netlib.cwrap('dgetrs_', null, ['number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number']);
                                    dgetrs(ptransA, pdim, pnrhs, pmatA, pldA, pipiv, pmatB, pldB, pinfo);
                                    //  matA.set(ddpmatA);
                                    // ipiv.set(iipipiv);
                                    matB.set(ddpmatB);
                                    info.set(iipinfo);
                                    this.netlib._free(ptransA);
                                    this.netlib._free(pdim);
                                    this.netlib._free(pnrhs);
                                    this.netlib._free(pmatA);
                                    this.netlib._free(pldA);
                                    this.netlib._free(pipiv);
                                    this.netlib._free(pmatB);
                                    this.netlib._free(pldB);
                                    this.netlib._free(pinfo);
                                };
                                JSBlas.prototype.dgetri = function (dim, matA, offsetA, ldA, ipiv, offsetIpiv, work, offsetWork, ldWork, info) {
                                    var pdim = this.netlib._malloc(4), pmatA = this.netlib._malloc(8 * matA.length), pldA = this.netlib._malloc(4), pipiv = this.netlib._malloc(4 * ipiv.length), pwork = this.netlib._malloc(8 * work.length), pldWork = this.netlib._malloc(4), pinfo = this.netlib._malloc(4 * info.length);
                                    this.netlib.setValue(pdim, dim, 'i32');
                                    var ddpmatA = new Float64Array(this.netlib.HEAPF64.buffer, pmatA, matA.length);
                                    ddpmatA.set(matA);
                                    this.netlib.setValue(pldA, ldA, 'i32');
                                    var iipipiv = new Int32Array(this.netlib.HEAP32.buffer, pipiv, ipiv.length);
                                    iipipiv.set(ipiv);
                                    var ddpwork = new Float64Array(this.netlib.HEAPF64.buffer, pwork, work.length);
                                    ddpwork.set(work);
                                    this.netlib.setValue(pldWork, ldWork, 'i32');
                                    var iipinfo = new Int32Array(this.netlib.HEAP32.buffer, pinfo, info.length);
                                    iipinfo.set(info);
                                    var dgetri = this.netlib.cwrap('dgetri_', null, ['number', 'number', 'number', 'number', 'number', 'number', 'number']);
                                    dgetri(pdim, pmatA, pldA, pipiv, pwork, pldWork, pinfo);
                                    matA.set(ddpmatA);
                                    //ipiv.set(iipipiv);
                                    work.set(ddpwork);
                                    info.set(iipinfo);
                                    this.netlib._free(pdim);
                                    this.netlib._free(pmatA);
                                    this.netlib._free(pldA);
                                    this.netlib._free(pipiv);
                                    this.netlib._free(pwork);
                                    this.netlib._free(pldWork);
                                    this.netlib._free(pinfo);
                                };
                                JSBlas.prototype.dgetrf = function (rows, columns, matA, offsetA, ldA, ipiv, offsetIpiv, info) {
                                    var prows = this.netlib._malloc(4), pcolumns = this.netlib._malloc(4), pmatA = this.netlib._malloc(8 * matA.length), pldA = this.netlib._malloc(4), pipiv = this.netlib._malloc(4 * ipiv.length), pinfo = this.netlib._malloc(4 * info.length);
                                    this.netlib.setValue(prows, rows, 'i32');
                                    this.netlib.setValue(pcolumns, columns, 'i32');
                                    var ddpmatA = new Float64Array(this.netlib.HEAPF64.buffer, pmatA, matA.length);
                                    ddpmatA.set(matA);
                                    this.netlib.setValue(pldA, ldA, 'i32');
                                    var iipipiv = new Int32Array(this.netlib.HEAP32.buffer, pipiv, ipiv.length);
                                    iipipiv.set(ipiv);
                                    var iipinfo = new Int32Array(this.netlib.HEAP32.buffer, pinfo, info.length);
                                    iipinfo.set(info);
                                    var dgetrf = this.netlib.cwrap('dgetrf_', null, ['number', 'number', 'number', 'number', 'number', 'number']);
                                    dgetrf(prows, pcolumns, pmatA, pldA, pipiv, pinfo);
                                    matA.set(ddpmatA);
                                    ipiv.set(iipipiv);
                                    info.set(iipinfo);
                                    this.netlib._free(prows);
                                    this.netlib._free(pcolumns);
                                    this.netlib._free(pmatA);
                                    this.netlib._free(pldA);
                                    this.netlib._free(pipiv);
                                    this.netlib._free(pinfo);
                                };
                                JSBlas.prototype.dorgqr = function (m, n, k, matA, offsetA, ldA, taw, offsetTaw, work, offsetWork, lWork, info) {
                                    var pm = this.netlib._malloc(4), pn = this.netlib._malloc(4), pk = this.netlib._malloc(4), pmatA = this.netlib._malloc(8 * matA.length), pldA = this.netlib._malloc(4), ptaw = this.netlib._malloc(8 * taw.length), pwork = this.netlib._malloc(8 * work.length), plWork = this.netlib._malloc(4), pinfo = this.netlib._malloc(4 * info.length);
                                    this.netlib.setValue(pm, m, 'i32');
                                    this.netlib.setValue(pn, n, 'i32');
                                    this.netlib.setValue(pk, k, 'i32');
                                    var ddpmatA = new Float64Array(this.netlib.HEAPF64.buffer, pmatA, matA.length);
                                    ddpmatA.set(matA);
                                    this.netlib.setValue(pldA, ldA, 'i32');
                                    var ddptaw = new Float64Array(this.netlib.HEAPF64.buffer, ptaw, taw.length);
                                    ddptaw.set(taw);
                                    var ddpwork = new Float64Array(this.netlib.HEAPF64.buffer, pwork, work.length);
                                    ddpwork.set(work);
                                    this.netlib.setValue(plWork, lWork, 'i32');
                                    var iipinfo = new Int32Array(this.netlib.HEAP32.buffer, pinfo, info.length);
                                    iipinfo.set(info);
                                    var dorgqr = this.netlib.cwrap('dorgqr_', null, ['number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number']);
                                    dorgqr(pm, pn, pk, pmatA, pldA, ptaw, pwork, plWork, pinfo);
                                    matA.set(ddpmatA);
                                    //taw.set(ddptaw);
                                    work.set(ddpwork);
                                    info.set(iipinfo);
                                    this.netlib._free(pm);
                                    this.netlib._free(pn);
                                    this.netlib._free(pk);
                                    this.netlib._free(pmatA);
                                    this.netlib._free(pldA);
                                    this.netlib._free(ptaw);
                                    this.netlib._free(pwork);
                                    this.netlib._free(plWork);
                                    this.netlib._free(pinfo);
                                };
                                JSBlas.prototype.dgeqrf = function (m, n, matA, offsetA, ldA, taw, offsetTaw, work, offsetwork, lWork, info) {
                                    var pm = this.netlib._malloc(4), pn = this.netlib._malloc(4), pmatA = this.netlib._malloc(8 * matA.length), pldA = this.netlib._malloc(4), ptaw = this.netlib._malloc(8 * taw.length), pwork = this.netlib._malloc(8 * work.length), plWork = this.netlib._malloc(4), pinfo = this.netlib._malloc(4 * info.length);
                                    this.netlib.setValue(pm, m, 'i32');
                                    this.netlib.setValue(pn, n, 'i32');
                                    var ddpmatA = new Float64Array(this.netlib.HEAPF64.buffer, pmatA, matA.length);
                                    ddpmatA.set(matA);
                                    this.netlib.setValue(pldA, ldA, 'i32');
                                    var ddptaw = new Float64Array(this.netlib.HEAPF64.buffer, ptaw, taw.length);
                                    ddptaw.set(taw);
                                    var ddpwork = new Float64Array(this.netlib.HEAPF64.buffer, pwork, work.length);
                                    ddpwork.set(work);
                                    this.netlib.setValue(plWork, lWork, 'i32');
                                    var iipinfo = new Int32Array(this.netlib.HEAP32.buffer, pinfo, info.length);
                                    iipinfo.set(info);
                                    var dgeqrf = this.netlib.cwrap('dgeqrf_', null, ['number', 'number', 'number', 'number', 'number', 'number', 'number', 'number']);
                                    dgeqrf(pm, pn, pmatA, pldA, ptaw, pwork, plWork, pinfo);
                                    matA.set(ddpmatA);
                                    taw.set(ddptaw);
                                    work.set(ddpwork);
                                    info.set(iipinfo);
                                    this.netlib._free(pm);
                                    this.netlib._free(pn);
                                    this.netlib._free(pmatA);
                                    this.netlib._free(pldA);
                                    this.netlib._free(ptaw);
                                    this.netlib._free(pwork);
                                    this.netlib._free(plWork);
                                    this.netlib._free(pinfo);
                                };
                                JSBlas.prototype.dgesdd = function (jobz, m, n, data, lda, s, u, ldu, vt, ldvt, work, length, iwork, info) {
                                    var pjobz = this.netlib._malloc(1), pm = this.netlib._malloc(4), pn = this.netlib._malloc(4), pdata = this.netlib._malloc(8 * data.length), plda = this.netlib._malloc(4), ps = this.netlib._malloc(8 * s.length), pu = this.netlib._malloc(8 * u.length), pldu = this.netlib._malloc(4), pvt = this.netlib._malloc(8 * vt.length), pldvt = this.netlib._malloc(4), pwork = this.netlib._malloc(8 * work.length), plength = this.netlib._malloc(4), piwork = this.netlib._malloc(4 * iwork.length), pinfo = this.netlib._malloc(4 * info.length);
                                    this.netlib.setValue(pjobz, jobz.charCodeAt(0), 'i8');
                                    this.netlib.setValue(pm, m, 'i32');
                                    this.netlib.setValue(pn, n, 'i32');
                                    var ddpdata = new Float64Array(this.netlib.HEAPF64.buffer, pdata, data.length);
                                    ddpdata.set(data);
                                    this.netlib.setValue(plda, lda, 'i32');
                                    var ddps = new Float64Array(this.netlib.HEAPF64.buffer, ps, s.length);
                                    ddps.set(s);
                                    var ddpu = new Float64Array(this.netlib.HEAPF64.buffer, pu, u.length);
                                    ddpu.set(u);
                                    this.netlib.setValue(pldu, ldu, 'i32');
                                    var ddpvt = new Float64Array(this.netlib.HEAPF64.buffer, pvt, vt.length);
                                    ddpvt.set(vt);
                                    this.netlib.setValue(pldvt, ldvt, 'i32');
                                    var ddpwork = new Float64Array(this.netlib.HEAPF64.buffer, pwork, work.length);
                                    ddpwork.set(work);
                                    this.netlib.setValue(plength, length, 'i32');
                                    var iipiwork = new Int32Array(this.netlib.HEAP32.buffer, piwork, iwork.length);
                                    iipiwork.set(iwork);
                                    var iipinfo = new Int32Array(this.netlib.HEAP32.buffer, pinfo, info.length);
                                    iipinfo.set(info);
                                    var dgesdd = this.netlib.cwrap('dgesdd_', null, ['number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number', 'number']);
                                    dgesdd(pjobz, pm, pn, pdata, plda, ps, pu, pldu, pvt, pldvt, pwork, plength, piwork, pinfo);
                                    data.set(ddpdata);
                                    s.set(ddps);
                                    u.set(ddpu);
                                    vt.set(ddpvt);
                                    work.set(ddpwork);
                                    iwork.set(iipiwork);
                                    info.set(iipinfo);
                                    this.netlib._free(pjobz);
                                    this.netlib._free(pm);
                                    this.netlib._free(pn);
                                    this.netlib._free(pdata);
                                    this.netlib._free(plda);
                                    this.netlib._free(ps);
                                    this.netlib._free(pu);
                                    this.netlib._free(pldu);
                                    this.netlib._free(pvt);
                                    this.netlib._free(pldvt);
                                    this.netlib._free(pwork);
                                    this.netlib._free(plength);
                                    this.netlib._free(piwork);
                                    this.netlib._free(pinfo);
                                };
                                JSBlas.prototype.connect = function () {
                                };
                                JSBlas.prototype.disconnect = function () {
                                };
                                return JSBlas;
                            }());
                            blas.JSBlas = JSBlas;
                        })(blas = blassolver.blas || (blassolver.blas = {}));
                    })(blassolver = matrix.blassolver || (matrix.blassolver = {}));
                })(matrix = common.matrix || (common.matrix = {}));
            })(common = ml.common || (ml.common = {}));
        })(ml = mwg.ml || (mwg.ml = {}));
    })(mwg = org.mwg || (org.mwg = {}));
})(org || (org = {}));
/// <reference path="mwg.d.ts" />
/// <reference path="mwg.ml.blas.ts" />
var org;
(function (org) {
    var mwg;
    (function (mwg) {
        var ml;
        (function (ml) {
            var AbstractMLNode = (function (_super) {
                __extends(AbstractMLNode, _super);
                function AbstractMLNode(p_world, p_time, p_id, p_graph, currentResolution) {
                    _super.call(this, p_world, p_time, p_id, p_graph, currentResolution);
                }
                AbstractMLNode.prototype.setProperty = function (propertyName, propertyType, propertyValue) {
                    _super.prototype.setProperty.call(this, propertyName, propertyType, propertyValue);
                };
                AbstractMLNode.prototype.get = function (propertyName) {
                    if (propertyName != null && propertyName.length > 0 && propertyName.charAt(0) == '$') {
                        var expressionObj = _super.prototype.get.call(this, propertyName.substring(1));
                        var localEngine = org.mwg.ml.common.mathexp.impl.MathExpressionEngine.parse(expressionObj.toString());
                        var variables = new java.util.HashMap();
                        variables.put("PI", Math.PI);
                        variables.put("TRUE", 1.0);
                        variables.put("FALSE", 0.0);
                        return localEngine.eval(this, variables);
                    }
                    else {
                        return _super.prototype.get.call(this, propertyName);
                    }
                };
                AbstractMLNode.prototype.extractFeatures = function (callback) { };
                AbstractMLNode.prototype.parseDouble = function (payload) {
                    return parseFloat(payload);
                };
                AbstractMLNode.FROM_SEPARATOR = ";";
                AbstractMLNode.FROM = "FROM";
                return AbstractMLNode;
            }(org.mwg.plugin.AbstractNode));
            ml.AbstractMLNode = AbstractMLNode;
            var algorithm;
            (function (algorithm) {
                var profiling;
                (function (profiling) {
                    var GaussianGmmNode = (function (_super) {
                        __extends(GaussianGmmNode, _super);
                        function GaussianGmmNode(p_world, p_time, p_id, p_graph, currentResolution) {
                            _super.call(this, p_world, p_time, p_id, p_graph, currentResolution);
                        }
                        GaussianGmmNode.prototype.setProperty = function (propertyName, propertyType, propertyValue) {
                            if (propertyName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.LEVEL_KEY) {
                                _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.INT);
                            }
                            else {
                                if (propertyName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.WIDTH_KEY) {
                                    _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.INT);
                                }
                                else {
                                    if (propertyName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_FACTOR_KEY) {
                                        _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.DOUBLE);
                                    }
                                    else {
                                        if (propertyName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.THRESHOLD_KEY) {
                                            _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.DOUBLE);
                                        }
                                        else {
                                            if (propertyName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.PRECISION_KEY) {
                                                _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.DOUBLE_ARRAY);
                                            }
                                            else {
                                                _super.prototype.setProperty.call(this, propertyName, propertyType, propertyValue);
                                            }
                                        }
                                    }
                                }
                            }
                        };
                        GaussianGmmNode.prototype.type = function (attributeName) {
                            if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.AVG) {
                                return org.mwg.Type.DOUBLE_ARRAY;
                            }
                            else {
                                if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.MIN) {
                                    return org.mwg.Type.DOUBLE_ARRAY;
                                }
                                else {
                                    if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.MAX) {
                                        return org.mwg.Type.DOUBLE_ARRAY;
                                    }
                                    else {
                                        if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.COV) {
                                            return org.mwg.Type.DOUBLE_ARRAY;
                                        }
                                        else {
                                            if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.PRECISION_KEY) {
                                                return org.mwg.Type.DOUBLE_ARRAY;
                                            }
                                            else {
                                                return _super.prototype.type.call(this, attributeName);
                                            }
                                        }
                                    }
                                }
                            }
                        };
                        GaussianGmmNode.prototype.get = function (attributeName) {
                            if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.AVG) {
                                return this.getAvg();
                            }
                            else {
                                if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.MIN) {
                                    return this.getMin();
                                }
                                else {
                                    if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.MAX) {
                                        return this.getMax();
                                    }
                                    else {
                                        if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.MAX) {
                                            return this.getMax();
                                        }
                                        else {
                                            if (attributeName === org.mwg.ml.algorithm.profiling.GaussianGmmNode.COV) {
                                                var resolved = this._resolver.resolveState(this, true);
                                                var initialPrecision = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianGmmNode.PRECISION_KEY);
                                                var nbfeature = this.getNumberOfFeatures();
                                                if (initialPrecision == null) {
                                                    initialPrecision = new Float64Array(nbfeature);
                                                    for (var i = 0; i < nbfeature; i++) {
                                                        initialPrecision[i] = 1;
                                                    }
                                                }
                                                return this.getCovariance(this.getAvg(), initialPrecision);
                                            }
                                            else {
                                                return _super.prototype.get.call(this, attributeName);
                                            }
                                        }
                                    }
                                }
                            }
                        };
                        GaussianGmmNode.prototype.learn = function (callback) {
                            var _this = this;
                            this.extractFeatures(function (values) {
                                _this.learnVector(values, callback);
                            });
                        };
                        GaussianGmmNode.prototype.learnVector = function (values, callback) {
                            var _this = this;
                            var resolved = this._resolver.resolveState(this, true);
                            var width = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.WIDTH_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.WIDTH_DEF);
                            var compressionFactor = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_FACTOR_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_FACTOR_DEF);
                            var compressionIter = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_ITER_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_ITER_DEF);
                            var initialPrecision = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianGmmNode.PRECISION_KEY);
                            if (initialPrecision == null) {
                                initialPrecision = new Float64Array(values.length);
                                for (var i = 0; i < values.length; i++) {
                                    initialPrecision[i] = 1;
                                }
                            }
                            var precisions = initialPrecision;
                            var threshold = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.THRESHOLD_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.THRESHOLD_DEF);
                            var creationTask = this.graph().newTask().then(function (context) {
                                var node = context.getVariable("starterNode");
                                node.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, true);
                            });
                            var traverse = this.graph().newTask();
                            traverse.fromVar("starterNode").traverse(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY).then(function (context) {
                                var result = context.getPreviousResult();
                                var parent = context.getVariable("starterNode");
                                var resultChild = _this.filter(result, values, precisions, threshold, parent.getLevel() - 1.0);
                                if (resultChild != null) {
                                    parent.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, false);
                                    context.setVariable("continueLoop", true);
                                    context.setVariable("starterNode", resultChild);
                                }
                                else {
                                    context.setVariable("continueLoop", false);
                                }
                            }).ifThen(function (context) {
                                return context.getVariable("continueLoop");
                            }, traverse);
                            var mainTask = this.graph().newTask().from(this).asVar("starterNode").wait(traverse).wait(creationTask);
                            mainTask.executeThen(function (context) {
                                if (callback != null) {
                                    callback(true);
                                }
                            });
                        };
                        GaussianGmmNode.prototype.checkInside = function (min, max, precisions, threshold, level) {
                            threshold = threshold + level * 0.707;
                            var avg = this.getAvg();
                            var result = true;
                            var cov = this.getCovarianceArray(avg, precisions);
                            for (var i = 0; i < min.length; i++) {
                                cov[i] = Math.sqrt(cov[i]);
                                if (((avg[i] + cov[i]) < (min[i] - threshold * precisions[i])) || ((avg[i] - cov[i]) > (max[i] + threshold * precisions[i]))) {
                                    result = false;
                                    break;
                                }
                            }
                            return result;
                        };
                        GaussianGmmNode.prototype.filter = function (result, features, precisions, threshold, level) {
                            threshold = threshold + level * 0.707;
                            if (result == null || result.length == 0) {
                                return null;
                            }
                            var distances = new Float64Array(result.length);
                            var min = java.lang.Double.MAX_VALUE;
                            var index = 0;
                            for (var i = 0; i < result.length; i++) {
                                var temp = result[i];
                                var avg = temp.getAvg();
                                distances[i] = org.mwg.ml.algorithm.profiling.GaussianGmmNode.distance(features, avg, temp.getCovarianceArray(avg, precisions));
                                if (distances[i] < min) {
                                    min = distances[i];
                                    index = i;
                                }
                            }
                            if (min < threshold) {
                                return result[index];
                            }
                            else {
                                return null;
                            }
                        };
                        GaussianGmmNode.prototype.predict = function (callback) { };
                        GaussianGmmNode.prototype.getLevel = function () {
                            return this._resolver.resolveState(this, true).getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.LEVEL_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.LEVEL_DEF);
                        };
                        GaussianGmmNode.prototype.getWidth = function () {
                            return this._resolver.resolveState(this, true).getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.WIDTH_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.WIDTH_DEF);
                        };
                        GaussianGmmNode.prototype.getCompressionFactor = function () {
                            return this._resolver.resolveState(this, true).getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_FACTOR_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_FACTOR_DEF);
                        };
                        GaussianGmmNode.prototype.getCompressionIter = function () {
                            return this._resolver.resolveState(this, true).getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_ITER_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.COMPRESSION_ITER_DEF);
                        };
                        GaussianGmmNode.prototype.updateLevel = function (newLevel) {
                            _super.prototype.set.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.LEVEL_KEY, newLevel);
                            if (newLevel == 0) {
                                _super.prototype.set.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, new Float64Array(0));
                            }
                            else {
                                _super.prototype.rel.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, function (result) {
                                    for (var i = 0; i < result.length; i++) {
                                        var g = result[i];
                                        g.updateLevel(newLevel - 1);
                                        result[i].free();
                                    }
                                });
                            }
                        };
                        GaussianGmmNode.prototype.createLevel = function (values, level, width, compressionFactor, compressionIter, precisions, threshold) {
                            var g = this.graph().newTypedNode(this.world(), this.time(), "GaussianGmm");
                            g.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.LEVEL_KEY, level);
                            g.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, false);
                            _super.prototype.add.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, g);
                            return g;
                        };
                        GaussianGmmNode.prototype.checkAndCompress = function (width, compressionFactor, compressionIter, precisions, threshold) {
                            var selfPointer = this;
                            var subgaussians = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY);
                            if (subgaussians != null && subgaussians.length >= compressionFactor * width) {
                                _super.prototype.rel.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, function (result) {
                                    var subgauss = new Array(result.length);
                                    var data = new Array(result.length);
                                    for (var i = 0; i < result.length; i++) {
                                        subgauss[i] = result[i];
                                        data[i] = subgauss[i].getAvg();
                                    }
                                    var clusteringEngine = new org.mwg.ml.algorithm.profiling.KMeans();
                                    var clusters = clusteringEngine.getClusterIds(data, width, compressionIter, precisions);
                                    var mainClusters = new Array(width);
                                    for (var i = 0; i < width; i++) {
                                        if (clusters[i] != null && clusters[i].length > 0) {
                                            var max = 0;
                                            var maxpos = 0;
                                            for (var j = 0; j < clusters[i].length; j++) {
                                                var x = subgauss[clusters[i][j]].getTotal();
                                                if (x > max) {
                                                    max = x;
                                                    maxpos = clusters[i][j];
                                                }
                                            }
                                            mainClusters[i] = subgauss[maxpos];
                                        }
                                    }
                                    for (var i = 0; i < width; i++) {
                                        if (clusters[i].length > 1 && mainClusters[i].getTotal() == 1 && mainClusters[i].getLevel() > 0) {
                                            mainClusters[i].createLevel(mainClusters[i].getAvg(), mainClusters[i].getLevel() - 1, width, compressionFactor, compressionIter, precisions, threshold).free();
                                        }
                                        if (clusters[i] != null && clusters[i].length > 0) {
                                            for (var j = 0; j < clusters[i].length; j++) {
                                                var g = subgauss[clusters[i][j]];
                                                if (g != mainClusters[i]) {
                                                    mainClusters[i].move(g);
                                                    selfPointer.remove(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, g);
                                                    g.free();
                                                }
                                            }
                                            mainClusters[i].checkAndCompress(width, compressionFactor, compressionIter, precisions, threshold);
                                        }
                                    }
                                    for (var i = 0; i < result.length; i++) {
                                        result[i].free();
                                    }
                                });
                            }
                        };
                        GaussianGmmNode.prototype.move = function (subgaus) {
                            var total = this.getTotal();
                            var level = this.getLevel();
                            var sum = this.getSum();
                            var min = this.getMin();
                            var max = this.getMax();
                            var sumsquares = this.getSumSquares();
                            total = total + subgaus.getTotal();
                            var sum2 = subgaus.getSum();
                            var min2 = subgaus.getMin();
                            var max2 = subgaus.getMax();
                            var sumsquares2 = subgaus.getSumSquares();
                            for (var i = 0; i < sum.length; i++) {
                                sum[i] = sum[i] + sum2[i];
                                if (min2[i] < min[i]) {
                                    min[i] = min2[i];
                                }
                                if (max2[i] > max[i]) {
                                    max[i] = max2[i];
                                }
                            }
                            for (var i = 0; i < sumsquares.length; i++) {
                                sumsquares[i] = sumsquares[i] + sumsquares2[i];
                            }
                            this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_TOTAL_KEY, total);
                            this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY, sum);
                            this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MIN_KEY, min);
                            this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MAX_KEY, max);
                            this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY, sumsquares);
                            if (level > 0) {
                                var subrelations = subgaus.get(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY);
                                if (subrelations == null) {
                                    subgaus.updateLevel(level - 1);
                                    _super.prototype.add.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, subgaus);
                                }
                                else {
                                    var oldrel = this.get(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY);
                                    if (oldrel == null) {
                                        oldrel = new Float64Array(0);
                                    }
                                    var newrelations = new Float64Array(oldrel.length + subrelations.length);
                                    java.lang.System.arraycopy(oldrel, 0, newrelations, 0, oldrel.length);
                                    java.lang.System.arraycopy(subrelations, 0, newrelations, oldrel.length, subrelations.length);
                                    this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY, newrelations);
                                }
                            }
                        };
                        GaussianGmmNode.prototype.query = function (level, min, max, callback) {
                            var nbfeature = this.getNumberOfFeatures();
                            if (nbfeature == 0) {
                                callback(null);
                                return;
                            }
                            var resolved = this._resolver.resolveState(this, true);
                            var initialPrecision = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianGmmNode.PRECISION_KEY);
                            if (initialPrecision == null) {
                                initialPrecision = new Float64Array(nbfeature);
                                for (var i = 0; i < nbfeature; i++) {
                                    initialPrecision[i] = 1;
                                }
                            }
                            if (min == null) {
                                min = this.getMin();
                            }
                            if (max == null) {
                                max = this.getMax();
                            }
                            for (var i = 0; i < nbfeature; i++) {
                                if ((max[i] - min[i]) < initialPrecision[i]) {
                                    min[i] = min[i] - initialPrecision[i];
                                    max[i] = min[i] + 2 * initialPrecision[i];
                                }
                            }
                            var finalMin = min;
                            var finalMax = max;
                            var err = initialPrecision;
                            var threshold = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianGmmNode.THRESHOLD_KEY, org.mwg.ml.algorithm.profiling.GaussianGmmNode.THRESHOLD_DEF);
                            var deepTraverseTask = this.graph().newTask();
                            var parentLevel = this.getLevel();
                            deepTraverseTask.from([this]);
                            for (var i = 0; i < this.getLevel() - level; i++) {
                                deepTraverseTask.traverseOrKeep(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY);
                                var finalI = i;
                                deepTraverseTask.select(function (node) {
                                    return node.checkInside(finalMin, finalMax, err, threshold, parentLevel - finalI);
                                });
                            }
                            deepTraverseTask.then(function (context) {
                                var leaves = context.getPreviousResult();
                                var covBackup = new org.mwg.ml.common.matrix.Matrix(null, nbfeature, nbfeature);
                                for (var i = 0; i < nbfeature; i++) {
                                    covBackup.set(i, i, err[i]);
                                }
                                var mvnBackup = new org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution(null, covBackup, false);
                                var totals = new Int32Array(leaves.length);
                                var globalTotal = 0;
                                var distributions = new Array(leaves.length);
                                for (var i = 0; i < leaves.length; i++) {
                                    var temp = leaves[i];
                                    totals[i] = temp.getTotal();
                                    globalTotal += totals[i];
                                    var avg = temp.getAvg();
                                    if (totals[i] > 2) {
                                        distributions[i] = new org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution(avg, temp.getCovariance(avg, err), false);
                                        distributions[i].setMin(temp.getMin());
                                        distributions[i].setMax(temp.getMax());
                                    }
                                    else {
                                        distributions[i] = mvnBackup.clone(avg);
                                    }
                                }
                                callback(new org.mwg.ml.algorithm.profiling.ProbaDistribution(totals, distributions, globalTotal));
                            });
                            deepTraverseTask.execute();
                        };
                        GaussianGmmNode.prototype.generateDistributions = function (level, callback) {
                            var nbfeature = this.getNumberOfFeatures();
                            if (nbfeature == 0) {
                                callback(null);
                                return;
                            }
                            var resolved = this._resolver.resolveState(this, true);
                            var initialPrecision = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianGmmNode.PRECISION_KEY);
                            if (initialPrecision == null) {
                                initialPrecision = new Float64Array(nbfeature);
                                for (var i = 0; i < nbfeature; i++) {
                                    initialPrecision[i] = 1;
                                }
                            }
                            var err = initialPrecision;
                            var deepTraverseTask = this.graph().newTask();
                            deepTraverseTask.from([this]);
                            for (var i = 0; i < this.getLevel() - level; i++) {
                                deepTraverseTask.traverseOrKeep(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY);
                            }
                            deepTraverseTask.then(function (context) {
                                var leaves = context.getPreviousResult();
                                var covBackup = new org.mwg.ml.common.matrix.Matrix(null, nbfeature, nbfeature);
                                for (var i = 0; i < nbfeature; i++) {
                                    covBackup.set(i, i, err[i]);
                                }
                                var mvnBackup = new org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution(null, covBackup, false);
                                var totals = new Int32Array(leaves.length);
                                var globalTotal = 0;
                                var distributions = new Array(leaves.length);
                                for (var i = 0; i < leaves.length; i++) {
                                    var temp = leaves[i];
                                    totals[i] = temp.getTotal();
                                    globalTotal += totals[i];
                                    var avg = temp.getAvg();
                                    if (totals[i] > 2) {
                                        distributions[i] = new org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution(avg, temp.getCovariance(avg, err), false);
                                        distributions[i].setMin(temp.getMin());
                                        distributions[i].setMax(temp.getMax());
                                    }
                                    else {
                                        distributions[i] = mvnBackup.clone(avg);
                                    }
                                }
                                callback(new org.mwg.ml.algorithm.profiling.ProbaDistribution(totals, distributions, globalTotal));
                            });
                            deepTraverseTask.execute();
                        };
                        GaussianGmmNode.prototype.toString = function () {
                            return this.get("name");
                        };
                        GaussianGmmNode.prototype.internallearn = function (values, width, compressionFactor, compressionIter, precisions, threshold, createNode) {
                            var features = values.length;
                            var reccursive = false;
                            var total = this.getTotal();
                            var level = this.getLevel();
                            if (total == 0) {
                                var sum = new Float64Array(features);
                                java.lang.System.arraycopy(values, 0, sum, 0, features);
                                total = 1;
                                this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_TOTAL_KEY, total);
                                this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY, sum);
                            }
                            else {
                                var sum;
                                var min;
                                var max;
                                var sumsquares;
                                if (total == 1) {
                                    sum = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                                    min = new Float64Array(features);
                                    max = new Float64Array(features);
                                    java.lang.System.arraycopy(sum, 0, min, 0, features);
                                    java.lang.System.arraycopy(sum, 0, max, 0, features);
                                    sumsquares = new Float64Array(features * (features + 1) / 2);
                                    var count = 0;
                                    for (var i = 0; i < features; i++) {
                                        for (var j = i; j < features; j++) {
                                            sumsquares[count] = sum[i] * sum[j];
                                            count++;
                                        }
                                    }
                                    if (createNode && level > 0) {
                                        var newLev = this.createLevel(sum, level - 1, width, compressionFactor, compressionIter, precisions, threshold);
                                        var d = org.mwg.ml.algorithm.profiling.GaussianGmmNode.distance(values, sum, precisions);
                                        if (d < threshold) {
                                            reccursive = true;
                                            newLev.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, createNode);
                                        }
                                        newLev.free();
                                    }
                                }
                                else {
                                    sum = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                                    min = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MIN_KEY);
                                    max = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MAX_KEY);
                                    sumsquares = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY);
                                }
                                for (var i = 0; i < features; i++) {
                                    if (values[i] < min[i]) {
                                        min[i] = values[i];
                                    }
                                    if (values[i] > max[i]) {
                                        max[i] = values[i];
                                    }
                                    sum[i] += values[i];
                                }
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    for (var j = i; j < features; j++) {
                                        sumsquares[count] += values[i] * values[j];
                                        count++;
                                    }
                                }
                                total++;
                                if (createNode && level > 0 && !reccursive) {
                                    var newLev = this.createLevel(values, level - 1, width, compressionFactor, compressionIter, precisions, threshold);
                                    newLev.free();
                                    this.checkAndCompress(width, compressionFactor, compressionIter, precisions, threshold);
                                }
                                this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_TOTAL_KEY, total);
                                this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY, sum);
                                this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MIN_KEY, min);
                                this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MAX_KEY, max);
                                this.set(org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY, sumsquares);
                            }
                        };
                        GaussianGmmNode.prototype.getNumberOfFeatures = function () {
                            var total = this.getTotal();
                            if (total == 0) {
                                return 0;
                            }
                            else {
                                var sum = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                                return sum.length;
                            }
                        };
                        GaussianGmmNode.prototype.getSum = function () {
                            var total = this.getTotal();
                            if (total == 0) {
                                return null;
                            }
                            else {
                                return _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                            }
                        };
                        GaussianGmmNode.prototype.getSumSquares = function () {
                            var total = this.getTotal();
                            if (total == 0) {
                                return null;
                            }
                            if (total == 1) {
                                var sum = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                                var features = sum.length;
                                var sumsquares = new Float64Array(features * (features + 1) / 2);
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    for (var j = i; j < features; j++) {
                                        sumsquares[count] = sum[i] * sum[j];
                                        count++;
                                    }
                                }
                                return sumsquares;
                            }
                            else {
                                return _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY);
                            }
                        };
                        GaussianGmmNode.prototype.getProbability = function (featArray, err, normalizeOnAvg) {
                            var sum = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                            var sumsquares = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY);
                            var mnd = org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution.getDistribution(sum, sumsquares, this.getTotal(), false);
                            if (mnd == null) {
                                return 0;
                            }
                            else {
                                return mnd.density(featArray, normalizeOnAvg);
                            }
                        };
                        GaussianGmmNode.prototype.getProbabilityArray = function (featArray, err, normalizeOnAvg) {
                            var res = new Float64Array(featArray.length);
                            var sum = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                            var sumsquares = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY);
                            var mnd = org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution.getDistribution(sum, sumsquares, this.getTotal(), false);
                            if (mnd == null) {
                                return res;
                            }
                            else {
                                for (var i = 0; i < res.length; i++) {
                                    res[i] = mnd.density(featArray[i], normalizeOnAvg);
                                }
                                return res;
                            }
                        };
                        GaussianGmmNode.prototype.getTotal = function () {
                            var x = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_TOTAL_KEY);
                            if (x == null) {
                                return 0;
                            }
                            else {
                                return x;
                            }
                        };
                        GaussianGmmNode.prototype.getAvg = function () {
                            var total = this.getTotal();
                            if (total == 0) {
                                return null;
                            }
                            if (total == 1) {
                                return _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                            }
                            else {
                                var avg = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                                for (var i = 0; i < avg.length; i++) {
                                    avg[i] = avg[i] / total;
                                }
                                return avg;
                            }
                        };
                        GaussianGmmNode.prototype.getCovarianceArray = function (avg, err) {
                            if (avg == null) {
                                var errClone = new Float64Array(err.length);
                                java.lang.System.arraycopy(err, 0, errClone, 0, err.length);
                                return errClone;
                            }
                            if (err == null) {
                                err = new Float64Array(avg.length);
                            }
                            var features = avg.length;
                            var total = this.getTotal();
                            if (total == 0) {
                                return null;
                            }
                            if (total > 1) {
                                var covariances = new Float64Array(features);
                                var sumsquares = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY);
                                var correction = total;
                                correction = correction / (total - 1);
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    covariances[i] = (sumsquares[count] / total - avg[i] * avg[i]) * correction;
                                    if (covariances[i] < err[i]) {
                                        covariances[i] = err[i];
                                    }
                                    count += features - i;
                                }
                                return covariances;
                            }
                            else {
                                var errClone = new Float64Array(err.length);
                                java.lang.System.arraycopy(err, 0, errClone, 0, err.length);
                                return errClone;
                            }
                        };
                        GaussianGmmNode.prototype.getCovariance = function (avg, err) {
                            var features = avg.length;
                            var total = this.getTotal();
                            if (total == 0) {
                                return null;
                            }
                            if (err == null) {
                                err = new Float64Array(avg.length);
                            }
                            if (total > 1) {
                                var covariances = new Float64Array(features * features);
                                var sumsquares = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUMSQUARE_KEY);
                                var correction = total;
                                correction = correction / (total - 1);
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    for (var j = i; j < features; j++) {
                                        covariances[i * features + j] = (sumsquares[count] / total - avg[i] * avg[j]) * correction;
                                        covariances[j * features + i] = covariances[i * features + j];
                                        count++;
                                        if (covariances[i * features + i] < err[i]) {
                                            covariances[i * features + i] = err[i];
                                        }
                                    }
                                }
                                return new org.mwg.ml.common.matrix.Matrix(covariances, features, features);
                            }
                            else {
                                return null;
                            }
                        };
                        GaussianGmmNode.prototype.getMin = function () {
                            var total = this.getTotal();
                            if (total == 0) {
                                return null;
                            }
                            if (total == 1) {
                                var min = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                                return min;
                            }
                            else {
                                var min = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MIN_KEY);
                                return min;
                            }
                        };
                        GaussianGmmNode.prototype.getMax = function () {
                            var total = this.getTotal();
                            if (total == 0) {
                                return null;
                            }
                            if (total == 1) {
                                var max = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUM_KEY);
                                return max;
                            }
                            else {
                                var max = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_MAX_KEY);
                                return max;
                            }
                        };
                        GaussianGmmNode.prototype.getSubGraph = function () {
                            var res = _super.prototype.get.call(this, org.mwg.ml.algorithm.profiling.GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY);
                            if (res == null) {
                                res = new Float64Array(0);
                            }
                            return res;
                        };
                        GaussianGmmNode.distance = function (features, avg, precisions) {
                            var max = 0;
                            var temp;
                            for (var i = 0; i < features.length; i++) {
                                temp = (features[i] - avg[i]) * (features[i] - avg[i]) / precisions[i];
                                if (temp > max) {
                                    max = temp;
                                }
                            }
                            return Math.sqrt(max);
                        };
                        GaussianGmmNode.NAME = "GaussianGmm";
                        GaussianGmmNode.MIN = "min";
                        GaussianGmmNode.MAX = "max";
                        GaussianGmmNode.AVG = "avg";
                        GaussianGmmNode.COV = "cov";
                        GaussianGmmNode.LEVEL_KEY = "_level";
                        GaussianGmmNode.LEVEL_DEF = 0;
                        GaussianGmmNode.WIDTH_KEY = "_width";
                        GaussianGmmNode.WIDTH_DEF = 10;
                        GaussianGmmNode.COMPRESSION_FACTOR_KEY = "_compression";
                        GaussianGmmNode.COMPRESSION_FACTOR_DEF = 2;
                        GaussianGmmNode.COMPRESSION_ITER_KEY = "_compressioniter";
                        GaussianGmmNode.COMPRESSION_ITER_DEF = 10;
                        GaussianGmmNode.THRESHOLD_KEY = "_threshold";
                        GaussianGmmNode.THRESHOLD_DEF = 3;
                        GaussianGmmNode.PRECISION_KEY = "_precision";
                        GaussianGmmNode.INTERNAL_SUBGAUSSIAN_KEY = "_subGaussian";
                        GaussianGmmNode.INTERNAL_SUM_KEY = "_sum";
                        GaussianGmmNode.INTERNAL_SUMSQUARE_KEY = "_sumSquare";
                        GaussianGmmNode.INTERNAL_TOTAL_KEY = "_total";
                        GaussianGmmNode.INTERNAL_MIN_KEY = "_min";
                        GaussianGmmNode.INTERNAL_MAX_KEY = "_max";
                        return GaussianGmmNode;
                    }(org.mwg.ml.AbstractMLNode));
                    profiling.GaussianGmmNode = GaussianGmmNode;
                    var GaussianGmmNode;
                    (function (GaussianGmmNode) {
                        var Factory = (function () {
                            function Factory() {
                            }
                            Factory.prototype.name = function () {
                                return org.mwg.ml.algorithm.profiling.GaussianGmmNode.NAME;
                            };
                            Factory.prototype.create = function (world, time, id, graph, initialResolution) {
                                return new org.mwg.ml.algorithm.profiling.GaussianGmmNode(world, time, id, graph, initialResolution);
                            };
                            return Factory;
                        }());
                        GaussianGmmNode.Factory = Factory;
                    })(GaussianGmmNode = profiling.GaussianGmmNode || (profiling.GaussianGmmNode = {}));
                    var GaussianSlotProfilingNode = (function (_super) {
                        __extends(GaussianSlotProfilingNode, _super);
                        function GaussianSlotProfilingNode(p_world, p_time, p_id, p_graph, currentResolution) {
                            _super.call(this, p_world, p_time, p_id, p_graph, currentResolution);
                        }
                        GaussianSlotProfilingNode.prototype.learn = function (callback) {
                            var _this = this;
                            this.extractFeatures(function (values) {
                                _this.learnArray(values);
                                callback(true);
                            });
                        };
                        GaussianSlotProfilingNode.prototype.learnArray = function (values) {
                            var resolved = this._resolver.resolveState(this, true);
                            var numOfSlot = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER, org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER_DEF);
                            var total;
                            var min;
                            var max;
                            var sum;
                            var sumSquare;
                            var features = values.length;
                            total = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_TOTAL_KEY);
                            if (numOfSlot == 1 || numOfSlot == 0) {
                                if (total == null) {
                                    resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_FEATURES_NUMBER, org.mwg.Type.INT, features);
                                    total = new Int32Array(1);
                                    min = new Float64Array(features);
                                    max = new Float64Array(features);
                                    sum = new Float64Array(features);
                                    sumSquare = new Float64Array(features * (features + 1) / 2);
                                }
                                else {
                                    min = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MIN_KEY);
                                    max = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MAX_KEY);
                                    sum = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUM_KEY);
                                    sumSquare = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUMSQUARE_KEY);
                                }
                                this.update(total, min, max, sum, sumSquare, values, 0, features, 0, 0);
                                return;
                            }
                            else {
                                if (total == null) {
                                    resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_FEATURES_NUMBER, org.mwg.Type.INT, features);
                                    total = new Int32Array(numOfSlot + 1);
                                    min = new Float64Array((numOfSlot + 1) * features);
                                    max = new Float64Array((numOfSlot + 1) * features);
                                    sum = new Float64Array((numOfSlot + 1) * features);
                                    sumSquare = new Float64Array((numOfSlot + 1) * features * (features + 1) / 2);
                                }
                                else {
                                    min = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MIN_KEY);
                                    max = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MAX_KEY);
                                    sum = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUM_KEY);
                                    sumSquare = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUMSQUARE_KEY);
                                }
                                var periodSize = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE, org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE_DEF);
                                var slot = org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.getIntTime(this.time(), numOfSlot, periodSize);
                                var index = slot * features;
                                var indexSquare = slot * features * (features + 1) / 2;
                                var indexTot = numOfSlot * features;
                                var indexSquareTot = numOfSlot * features * (features + 1) / 2;
                                this.update(total, min, max, sum, sumSquare, values, slot, features, index, indexSquare);
                                this.update(total, min, max, sum, sumSquare, values, numOfSlot, features, indexTot, indexSquareTot);
                            }
                            resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_FEATURES_NUMBER, org.mwg.Type.INT, features);
                            resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_TOTAL_KEY, org.mwg.Type.INT_ARRAY, total);
                            resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MIN_KEY, org.mwg.Type.DOUBLE_ARRAY, min);
                            resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MAX_KEY, org.mwg.Type.DOUBLE_ARRAY, max);
                            resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUM_KEY, org.mwg.Type.DOUBLE_ARRAY, sum);
                            resolved.setFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUMSQUARE_KEY, org.mwg.Type.DOUBLE_ARRAY, sumSquare);
                        };
                        GaussianSlotProfilingNode.prototype.predict = function (callback) {
                            var resolved = this.unphasedState();
                            var features = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_FEATURES_NUMBER, 0);
                            if (features == 0) {
                                callback(null);
                                return;
                            }
                            var numOfSlot = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER, org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER_DEF);
                            var periodSize = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE, org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE_DEF);
                            var slot = org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.getIntTime(this.time(), numOfSlot, periodSize);
                            var index = slot * features;
                            var total = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_TOTAL_KEY);
                            var sum = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUM_KEY);
                            var result = new Float64Array(features);
                            if (total != null) {
                                if (total[slot] != 0) {
                                    for (var j = 0; j < features; j++) {
                                        result[j] = sum[j + index] / total[slot];
                                    }
                                }
                            }
                            callback(result);
                        };
                        GaussianSlotProfilingNode.prototype.setProperty = function (propertyName, propertyType, propertyValue) {
                            if (propertyName === org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER) {
                                _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.INT);
                            }
                            else {
                                if (propertyName === org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE) {
                                    _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.LONG);
                                }
                                else {
                                    _super.prototype.setProperty.call(this, propertyName, propertyType, propertyValue);
                                }
                            }
                        };
                        GaussianSlotProfilingNode.prototype.get = function (attributeName) {
                            var state = this._resolver.resolveState(this, true);
                            if (attributeName === org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER) {
                                return state.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER, org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER_DEF);
                            }
                            else {
                                if (attributeName === org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE) {
                                    return state.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE, org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.PERIOD_SIZE_DEF);
                                }
                                else {
                                    return _super.prototype.get.call(this, attributeName);
                                }
                            }
                        };
                        GaussianSlotProfilingNode.getIntTime = function (time, numOfSlot, periodSize) {
                            if (numOfSlot <= 1) {
                                return 0;
                            }
                            var res = time % periodSize;
                            res = res / (periodSize / numOfSlot);
                            return res;
                        };
                        GaussianSlotProfilingNode.prototype.update = function (total, min, max, sum, sumSquare, values, slot, features, index, indexSquare) {
                            if (total[slot] == 1) {
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    min[index + i] = values[i];
                                    max[index + i] = values[i];
                                    sum[index + i] = values[i];
                                    for (var j = i; j < features; j++) {
                                        sumSquare[indexSquare + count] += values[i] * values[j];
                                        count++;
                                    }
                                }
                            }
                            else {
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    if (values[i] < min[index + i]) {
                                        min[index + i] = values[i];
                                    }
                                    if (values[i] > max[index + i]) {
                                        max[index + i] = values[i];
                                    }
                                    sum[index + i] += values[i];
                                    for (var j = i; j < features; j++) {
                                        sumSquare[indexSquare + count] += values[i] * values[j];
                                        count++;
                                    }
                                }
                            }
                            total[slot] += 1;
                        };
                        GaussianSlotProfilingNode.prototype.getMin = function () {
                            return this.unphasedState().getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MIN_KEY);
                        };
                        GaussianSlotProfilingNode.prototype.getMax = function () {
                            return this.unphasedState().getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_MAX_KEY);
                        };
                        GaussianSlotProfilingNode.prototype.getSum = function () {
                            return this.unphasedState().getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUM_KEY);
                        };
                        GaussianSlotProfilingNode.prototype.getSumSquare = function () {
                            return this.unphasedState().getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUMSQUARE_KEY);
                        };
                        GaussianSlotProfilingNode.prototype.getTotal = function () {
                            return this.unphasedState().getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_TOTAL_KEY);
                        };
                        GaussianSlotProfilingNode.prototype.getAvg = function () {
                            var resolved = this.unphasedState();
                            var numOfSlot = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER, org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.SLOTS_NUMBER_DEF);
                            var features = resolved.getFromKeyWithDefault(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_FEATURES_NUMBER, 0);
                            if (features == 0) {
                                return null;
                            }
                            var total = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_TOTAL_KEY);
                            var sum = resolved.getFromKey(org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.INTERNAL_SUM_KEY);
                            var result = new Float64Array(sum.length);
                            if (total != null) {
                                if (numOfSlot > 1) {
                                    var count = 0;
                                    for (var i = 0; i < (numOfSlot + 1); i++) {
                                        if (total[i] != 0) {
                                            for (var j = 0; j < features; j++) {
                                                result[count] = sum[count] / total[i];
                                                count++;
                                            }
                                        }
                                        else {
                                            count += features;
                                        }
                                    }
                                }
                                else {
                                    if (total[0] != 0) {
                                        for (var j = 0; j < features; j++) {
                                            result[j] = sum[j] / total[0];
                                        }
                                    }
                                }
                            }
                            return result;
                        };
                        GaussianSlotProfilingNode.NAME = "GaussianSlotProfiling";
                        GaussianSlotProfilingNode.SLOTS_NUMBER = "SLOTS_NUMBER";
                        GaussianSlotProfilingNode.SLOTS_NUMBER_DEF = 1;
                        GaussianSlotProfilingNode.PERIOD_SIZE = "PERIOD_SIZE";
                        GaussianSlotProfilingNode.PERIOD_SIZE_DEF = 24 * 3600 * 1000;
                        GaussianSlotProfilingNode.INTERNAL_FEATURES_NUMBER = "_featuresNb";
                        GaussianSlotProfilingNode.INTERNAL_TOTAL_KEY = "_total";
                        GaussianSlotProfilingNode.INTERNAL_MIN_KEY = "_min";
                        GaussianSlotProfilingNode.INTERNAL_MAX_KEY = "_max";
                        GaussianSlotProfilingNode.INTERNAL_SUM_KEY = "_sum";
                        GaussianSlotProfilingNode.INTERNAL_SUMSQUARE_KEY = "_sumSquare";
                        return GaussianSlotProfilingNode;
                    }(org.mwg.ml.AbstractMLNode));
                    profiling.GaussianSlotProfilingNode = GaussianSlotProfilingNode;
                    var GaussianSlotProfilingNode;
                    (function (GaussianSlotProfilingNode) {
                        var Factory = (function () {
                            function Factory() {
                            }
                            Factory.prototype.name = function () {
                                return org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode.NAME;
                            };
                            Factory.prototype.create = function (world, time, id, graph, initialResolution) {
                                return new org.mwg.ml.algorithm.profiling.GaussianSlotProfilingNode(world, time, id, graph, initialResolution);
                            };
                            return Factory;
                        }());
                        GaussianSlotProfilingNode.Factory = Factory;
                    })(GaussianSlotProfilingNode = profiling.GaussianSlotProfilingNode || (profiling.GaussianSlotProfilingNode = {}));
                    var KMeans = (function () {
                        function KMeans() {
                        }
                        KMeans.prototype.getClusterIds = function (data, numOfCluster, numOfIterations, div) {
                            var result = new Array(numOfCluster);
                            var features = data[0].length;
                            var totals = new Int32Array(numOfCluster);
                            var centroids = new Array(numOfCluster);
                            for (var i = 0; i < numOfCluster; i++) {
                                centroids[i] = new Float64Array(features);
                            }
                            var categories = new Int32Array(data.length);
                            for (var i = 0; i < numOfCluster; i++) {
                                java.lang.System.arraycopy(data[i], 0, centroids[i], 0, features);
                            }
                            for (var iter = 0; iter < numOfIterations; iter++) {
                                for (var i = 0; i < totals.length; i++) {
                                    totals[i] = 0;
                                }
                                for (var i = 0; i < data.length; i++) {
                                    categories[i] = this.calculateCategory(data[i], centroids, div);
                                    totals[categories[i]]++;
                                }
                                for (var i = 0; i < centroids.length; i++) {
                                    for (var j = 0; j < features; j++) {
                                        centroids[i][j] = 0;
                                    }
                                }
                                for (var i = 0; i < data.length; i++) {
                                    for (var j = 0; j < features; j++) {
                                        centroids[categories[i]][j] += data[i][j];
                                    }
                                }
                                for (var i = 0; i < centroids.length; i++) {
                                    if (totals[i] != 0) {
                                        for (var j = 0; j < features; j++) {
                                            centroids[i][j] = centroids[i][j] / totals[i];
                                        }
                                    }
                                    else {
                                        var rand = new java.util.Random();
                                        var avg = data[rand.nextInt(data.length)];
                                        java.lang.System.arraycopy(avg, 0, centroids[i], 0, features);
                                    }
                                }
                            }
                            for (var i = 0; i < numOfCluster; i++) {
                                result[i] = new Int32Array(totals[i]);
                                var k = 0;
                                for (var j = 0; j < data.length; j++) {
                                    if (categories[j] == i) {
                                        result[i][k] = j;
                                        k++;
                                    }
                                }
                            }
                            return result;
                        };
                        KMeans.prototype.calculateCategory = function (values, centroids, div) {
                            var min = java.lang.Double.MAX_VALUE;
                            var pos = 0;
                            for (var i = 0; i < centroids.length; i++) {
                                var d = org.mwg.ml.algorithm.profiling.KMeans.distance(values, centroids[i], div);
                                if (d < min) {
                                    min = d;
                                    pos = i;
                                }
                            }
                            return pos;
                        };
                        KMeans.distance = function (features, avg, precisions) {
                            var max = 0;
                            var temp;
                            for (var i = 0; i < features.length; i++) {
                                temp = (features[i] - avg[i]) * (features[i] - avg[i]) / precisions[i];
                                if (temp > max) {
                                    max = temp;
                                }
                            }
                            return Math.sqrt(max);
                        };
                        return KMeans;
                    }());
                    profiling.KMeans = KMeans;
                    var ProbaDistribution = (function () {
                        function ProbaDistribution(total, distributions, global) {
                            this.total = total;
                            this.distributions = distributions;
                            this.global = global;
                        }
                        ProbaDistribution.prototype.calculate = function (features) {
                            var result = 0;
                            if (this.total != null) {
                                for (var j = 0; j < this.distributions.length; j++) {
                                    if (org.mwg.ml.algorithm.profiling.KMeans.distance(features, this.distributions[j].getAvg(), this.distributions[j].getCovDiag()) < 5) {
                                        result += this.distributions[j].density(features, false) * this.total[j] / this.global;
                                    }
                                }
                            }
                            else {
                                for (var j = 0; j < this.distributions.length; j++) {
                                    if (org.mwg.ml.algorithm.profiling.KMeans.distance(features, this.distributions[j].getAvg(), this.distributions[j].getCovDiag()) < 5) {
                                        result += this.distributions[j].density(features, false) / this.global;
                                    }
                                }
                            }
                            if (result > 1) {
                                result = 1;
                            }
                            return result;
                        };
                        ProbaDistribution.prototype.calculateArray = function (features, reporter) {
                            if (reporter != null) {
                                reporter.updateGraphInfo("Number of distributions: " + this.distributions.length + " , values: " + this.global);
                            }
                            var result = new Float64Array(features.length);
                            var calibration = 0;
                            for (var i = 0; i < features.length; i++) {
                                result[i] = this.calculate(features[i]);
                                calibration += result[i];
                                if (reporter != null) {
                                    var progress = i * (1.0 / (features.length));
                                    progress = progress * 50 + 50;
                                    reporter.updateProgress(progress);
                                    if (reporter.isCancelled()) {
                                        return null;
                                    }
                                }
                            }
                            if (calibration != 0) {
                                for (var i = 0; i < features.length; i++) {
                                    result[i] = result[i] / calibration;
                                }
                            }
                            return result;
                        };
                        ProbaDistribution.prototype.addUpProbabilities = function (features) {
                            var res = 0;
                            for (var i = 0; i < features.length; i++) {
                                res += this.calculate(features[i]);
                            }
                            return res;
                        };
                        ProbaDistribution.prototype.compareProbaDistribution = function (other, features) {
                            var error = new Float64Array(2);
                            var res1 = this.calculateArray(features, null);
                            var res2 = other.calculateArray(features, null);
                            var temp = 0;
                            for (var i = 0; i < res1.length; i++) {
                                temp = Math.abs(res1[i] - res2[i]);
                                error[0] += temp;
                                if (temp > error[1]) {
                                    error[1] = temp;
                                }
                            }
                            error[0] = error[0] / res1.length;
                            return error;
                        };
                        ProbaDistribution.prototype.ParallelCalculate = function (space, progressReporter) {
                            var result = new org.mwg.ml.common.NDimentionalArray();
                            try {
                            }
                            catch ($ex$) {
                                if ($ex$ instanceof Error) {
                                    var e = $ex$;
                                    console.error(e);
                                }
                                else {
                                    throw $ex$;
                                }
                            }
                            return result;
                        };
                        return ProbaDistribution;
                    }());
                    profiling.ProbaDistribution = ProbaDistribution;
                })(profiling = algorithm.profiling || (algorithm.profiling = {}));
                var regression;
                (function (regression) {
                    var PolynomialNode = (function (_super) {
                        __extends(PolynomialNode, _super);
                        function PolynomialNode(p_world, p_time, p_id, p_graph, currentResolution) {
                            _super.call(this, p_world, p_time, p_id, p_graph, currentResolution);
                        }
                        PolynomialNode.prototype.learn = function (value, callback) {
                            var previousState = this.unphasedState();
                            var timeOrigin = previousState.time();
                            var nodeTime = this.time();
                            var precision = previousState.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.PRECISION_KEY);
                            var weight = previousState.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY);
                            if (weight == null) {
                                weight = new Float64Array(1);
                                weight[0] = value;
                                previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY, org.mwg.Type.DOUBLE_ARRAY, weight);
                                previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_NB_PAST_KEY, org.mwg.Type.INT, 1);
                                previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_STEP_KEY, org.mwg.Type.LONG, 0);
                                previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_LAST_TIME_KEY, org.mwg.Type.LONG, 0);
                                callback(true);
                                return;
                            }
                            var stp = previousState.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_STEP_KEY);
                            var lastTime = nodeTime - timeOrigin;
                            if (stp == null || stp == 0) {
                                if (lastTime == 0) {
                                    weight = new Float64Array(1);
                                    weight[0] = value;
                                    previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY, org.mwg.Type.DOUBLE_ARRAY, weight);
                                    callback(true);
                                    return;
                                }
                                else {
                                    stp = lastTime;
                                    previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_STEP_KEY, org.mwg.Type.LONG, stp);
                                }
                            }
                            var deg = weight.length - 1;
                            var num = previousState.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_NB_PAST_KEY);
                            var t = (nodeTime - timeOrigin);
                            t = t / stp;
                            var maxError = this.maxErr(precision, deg);
                            if (Math.abs(org.mwg.ml.common.matrix.operation.PolynomialFit.extrapolate(t, weight) - value) <= maxError) {
                                previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_NB_PAST_KEY, org.mwg.Type.INT, num + 1);
                                previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_LAST_TIME_KEY, org.mwg.Type.LONG, lastTime);
                                callback(true);
                                return;
                            }
                            var previousTime = timeOrigin + previousState.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_LAST_TIME_KEY);
                            var factor;
                            if (nodeTime > previousTime) {
                                factor = 1;
                            }
                            else {
                                factor = 3;
                            }
                            var newMaxDegree = Math.min(num, org.mwg.ml.algorithm.regression.PolynomialNode._maxDegree);
                            if (deg < newMaxDegree) {
                                deg++;
                                var times = new Float64Array(factor * num + 1);
                                var values = new Float64Array(factor * num + 1);
                                var inc = 0;
                                if (num > 1) {
                                    inc = previousState.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_LAST_TIME_KEY);
                                    inc = inc / (stp * (factor * num - 1));
                                }
                                for (var i = 0; i < factor * num; i++) {
                                    times[i] = i * inc;
                                    values[i] = org.mwg.ml.common.matrix.operation.PolynomialFit.extrapolate(times[i], weight);
                                }
                                times[factor * num] = (nodeTime - timeOrigin) / stp;
                                values[factor * num] = value;
                                var pf = new org.mwg.ml.common.matrix.operation.PolynomialFit(deg);
                                pf.fit(times, values);
                                if (this.tempError(pf.getCoef(), times, values) <= maxError) {
                                    weight = pf.getCoef();
                                    previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY, org.mwg.Type.DOUBLE_ARRAY, weight);
                                    previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_NB_PAST_KEY, org.mwg.Type.INT, num + 1);
                                    previousState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_LAST_TIME_KEY, org.mwg.Type.LONG, lastTime);
                                    callback(true);
                                    return;
                                }
                            }
                            if (nodeTime > previousTime) {
                                var newstep = nodeTime - previousTime;
                                var phasedState = this.newState(previousTime);
                                var values = new Float64Array(2);
                                var pt = previousTime - timeOrigin;
                                pt = pt / stp;
                                values[0] = org.mwg.ml.common.matrix.operation.PolynomialFit.extrapolate(pt, weight);
                                values[1] = value;
                                maxError = this.maxErr(precision, 0);
                                if (Math.abs(values[1] - values[0]) <= maxError) {
                                    weight = new Float64Array(1);
                                    weight[0] = values[0];
                                }
                                else {
                                    values[1] = values[1] - values[0];
                                    weight = values;
                                }
                                phasedState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.PRECISION_KEY, org.mwg.Type.DOUBLE, precision);
                                phasedState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY, org.mwg.Type.DOUBLE_ARRAY, weight);
                                phasedState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_NB_PAST_KEY, org.mwg.Type.INT, 2);
                                phasedState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_STEP_KEY, org.mwg.Type.LONG, newstep);
                                phasedState.setFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_LAST_TIME_KEY, org.mwg.Type.LONG, newstep);
                                callback(true);
                                return;
                            }
                            else {
                            }
                            callback(false);
                            return;
                        };
                        PolynomialNode.prototype.extrapolate = function (callback) {
                            var time = this.time();
                            var state = this.unphasedState();
                            var timeOrigin = state.time();
                            var weight = state.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY);
                            if (weight == null) {
                                callback(0.0);
                                return;
                            }
                            var inferSTEP = state.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_STEP_KEY);
                            if (inferSTEP == null || inferSTEP == 0) {
                                callback(weight[0]);
                                return;
                            }
                            var t = (time - timeOrigin);
                            t = t / inferSTEP;
                            callback(org.mwg.ml.common.matrix.operation.PolynomialFit.extrapolate(t, weight));
                        };
                        PolynomialNode.prototype.setProperty = function (propertyName, propertyType, propertyValue) {
                            if (propertyName === org.mwg.ml.algorithm.regression.PolynomialNode.PRECISION_KEY) {
                                _super.prototype.setPropertyWithType.call(this, propertyName, propertyType, propertyValue, org.mwg.Type.DOUBLE);
                            }
                            else {
                                _super.prototype.setProperty.call(this, propertyName, propertyType, propertyValue);
                            }
                        };
                        PolynomialNode.prototype.getPrecision = function () {
                            return this.unphasedState().getFromKeyWithDefault(org.mwg.ml.algorithm.regression.PolynomialNode.PRECISION_KEY, org.mwg.ml.algorithm.regression.PolynomialNode.PRECISION_DEF);
                        };
                        PolynomialNode.prototype.getWeight = function () {
                            return this.unphasedState().getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY);
                        };
                        PolynomialNode.prototype.maxErr = function (precision, degree) {
                            return precision / Math.pow(2, degree + 2);
                        };
                        PolynomialNode.prototype.tempError = function (computedWeights, times, values) {
                            var maxErr = 0;
                            var temp;
                            for (var i = 0; i < times.length; i++) {
                                temp = Math.abs(values[i] - org.mwg.ml.common.matrix.operation.PolynomialFit.extrapolate(times[i], computedWeights));
                                if (temp > maxErr) {
                                    maxErr = temp;
                                }
                            }
                            return maxErr;
                        };
                        PolynomialNode.prototype.getDegree = function () {
                            var weights = this.getWeight();
                            if (weights == null) {
                                return -1;
                            }
                            else {
                                return weights.length - 1;
                            }
                        };
                        PolynomialNode.prototype.toString = function () {
                            var builder = new java.lang.StringBuilder();
                            builder.append("{\"world\":");
                            builder.append(this.world());
                            builder.append(",\"time\":");
                            builder.append(this.time());
                            builder.append(",\"id\":");
                            builder.append(this.id());
                            var state = this._resolver.resolveState(this, true);
                            if (state != null) {
                                builder.append(",\"data\": {");
                                var weight = state.getFromKey(org.mwg.ml.algorithm.regression.PolynomialNode.INTERNAL_WEIGHT_KEY);
                                if (weight != null) {
                                    builder.append("\"polynomial\": \"");
                                    for (var i = 0; i < weight.length; i++) {
                                        if (i != 0) {
                                            builder.append("+(");
                                        }
                                        builder.append(weight[i]);
                                        if (i == 1) {
                                            builder.append("*t");
                                        }
                                        else {
                                            if (i > 1) {
                                                builder.append("*t^" + i);
                                            }
                                        }
                                        if (i != 0) {
                                            builder.append(")");
                                        }
                                    }
                                    builder.append("\"");
                                }
                                builder.append("}}");
                            }
                            return builder.toString();
                        };
                        PolynomialNode.NAME = "Polynomial";
                        PolynomialNode.PRECISION_KEY = "PRECISION";
                        PolynomialNode.PRECISION_DEF = 1;
                        PolynomialNode.FEATURES_KEY = "FEATURES";
                        PolynomialNode.INTERNAL_WEIGHT_KEY = "_weight";
                        PolynomialNode.INTERNAL_STEP_KEY = "_step";
                        PolynomialNode.INTERNAL_NB_PAST_KEY = "_nb";
                        PolynomialNode.INTERNAL_LAST_TIME_KEY = "_lastTime";
                        PolynomialNode._maxDegree = 20;
                        return PolynomialNode;
                    }(org.mwg.ml.AbstractMLNode));
                    regression.PolynomialNode = PolynomialNode;
                    var PolynomialNode;
                    (function (PolynomialNode) {
                        var Factory = (function () {
                            function Factory() {
                            }
                            Factory.prototype.name = function () {
                                return org.mwg.ml.algorithm.regression.PolynomialNode.NAME;
                            };
                            Factory.prototype.create = function (world, time, id, graph, initialResolution) {
                                return new org.mwg.ml.algorithm.regression.PolynomialNode(world, time, id, graph, initialResolution);
                            };
                            return Factory;
                        }());
                        PolynomialNode.Factory = Factory;
                    })(PolynomialNode = regression.PolynomialNode || (regression.PolynomialNode = {}));
                })(regression = algorithm.regression || (algorithm.regression = {}));
            })(algorithm = ml.algorithm || (ml.algorithm = {}));
            var common;
            (function (common) {
                var mathexp;
                (function (mathexp) {
                    var impl;
                    (function (impl) {
                        var MathDoubleToken = (function () {
                            function MathDoubleToken(_content) {
                                this._content = _content;
                            }
                            MathDoubleToken.prototype.type = function () {
                                return 2;
                            };
                            MathDoubleToken.prototype.content = function () {
                                return this._content;
                            };
                            return MathDoubleToken;
                        }());
                        impl.MathDoubleToken = MathDoubleToken;
                        var MathEntities = (function () {
                            function MathEntities() {
                                this.operators = new java.util.HashMap();
                                this.operators.put("+", new org.mwg.ml.common.mathexp.impl.MathOperation("+", 20, true));
                                this.operators.put("-", new org.mwg.ml.common.mathexp.impl.MathOperation("-", 20, true));
                                this.operators.put("*", new org.mwg.ml.common.mathexp.impl.MathOperation("*", 30, true));
                                this.operators.put("/", new org.mwg.ml.common.mathexp.impl.MathOperation("/", 30, true));
                                this.operators.put("%", new org.mwg.ml.common.mathexp.impl.MathOperation("%", 30, true));
                                this.operators.put("^", new org.mwg.ml.common.mathexp.impl.MathOperation("^", 40, false));
                                this.operators.put("&&", new org.mwg.ml.common.mathexp.impl.MathOperation("&&", 4, false));
                                this.operators.put("||", new org.mwg.ml.common.mathexp.impl.MathOperation("||", 2, false));
                                this.operators.put(">", new org.mwg.ml.common.mathexp.impl.MathOperation(">", 10, false));
                                this.operators.put(">=", new org.mwg.ml.common.mathexp.impl.MathOperation(">=", 10, false));
                                this.operators.put("<", new org.mwg.ml.common.mathexp.impl.MathOperation("<", 10, false));
                                this.operators.put("<=", new org.mwg.ml.common.mathexp.impl.MathOperation("<=", 10, false));
                                this.operators.put("==", new org.mwg.ml.common.mathexp.impl.MathOperation("==", 7, false));
                                this.operators.put("!=", new org.mwg.ml.common.mathexp.impl.MathOperation("!=", 7, false));
                                this.functions = new java.util.HashMap();
                                this.functions.put("NOT", new org.mwg.ml.common.mathexp.impl.MathFunction("NOT", 1));
                                this.functions.put("IF", new org.mwg.ml.common.mathexp.impl.MathFunction("IF", 3));
                                this.functions.put("RAND", new org.mwg.ml.common.mathexp.impl.MathFunction("RAND", 0));
                                this.functions.put("SIN", new org.mwg.ml.common.mathexp.impl.MathFunction("SIN", 1));
                                this.functions.put("COS", new org.mwg.ml.common.mathexp.impl.MathFunction("COS", 1));
                                this.functions.put("TAN", new org.mwg.ml.common.mathexp.impl.MathFunction("TAN", 1));
                                this.functions.put("ASIN", new org.mwg.ml.common.mathexp.impl.MathFunction("ASIN", 1));
                                this.functions.put("ACOS", new org.mwg.ml.common.mathexp.impl.MathFunction("ACOS", 1));
                                this.functions.put("ATAN", new org.mwg.ml.common.mathexp.impl.MathFunction("ATAN", 1));
                                this.functions.put("MAX", new org.mwg.ml.common.mathexp.impl.MathFunction("MAX", 2));
                                this.functions.put("MIN", new org.mwg.ml.common.mathexp.impl.MathFunction("MIN", 2));
                                this.functions.put("ABS", new org.mwg.ml.common.mathexp.impl.MathFunction("ABS", 1));
                                this.functions.put("LOG", new org.mwg.ml.common.mathexp.impl.MathFunction("LOG", 1));
                                this.functions.put("ROUND", new org.mwg.ml.common.mathexp.impl.MathFunction("ROUND", 2));
                                this.functions.put("FLOOR", new org.mwg.ml.common.mathexp.impl.MathFunction("FLOOR", 1));
                                this.functions.put("CEILING", new org.mwg.ml.common.mathexp.impl.MathFunction("CEILING", 1));
                                this.functions.put("SQRT", new org.mwg.ml.common.mathexp.impl.MathFunction("SQRT", 1));
                                this.functions.put("SECONDS", new org.mwg.ml.common.mathexp.impl.MathFunction("SECONDS", 1));
                                this.functions.put("MINUTES", new org.mwg.ml.common.mathexp.impl.MathFunction("MINUTES", 1));
                                this.functions.put("HOURS", new org.mwg.ml.common.mathexp.impl.MathFunction("HOURS", 1));
                                this.functions.put("DAY", new org.mwg.ml.common.mathexp.impl.MathFunction("DAY", 1));
                                this.functions.put("MONTH", new org.mwg.ml.common.mathexp.impl.MathFunction("MONTH", 1));
                                this.functions.put("YEAR", new org.mwg.ml.common.mathexp.impl.MathFunction("YEAR", 1));
                                this.functions.put("DAYOFWEEK", new org.mwg.ml.common.mathexp.impl.MathFunction("DAYOFWEEK", 1));
                            }
                            MathEntities.getINSTANCE = function () {
                                if (org.mwg.ml.common.mathexp.impl.MathEntities.INSTANCE == null) {
                                    org.mwg.ml.common.mathexp.impl.MathEntities.INSTANCE = new org.mwg.ml.common.mathexp.impl.MathEntities();
                                }
                                return org.mwg.ml.common.mathexp.impl.MathEntities.INSTANCE;
                            };
                            MathEntities.INSTANCE = null;
                            return MathEntities;
                        }());
                        impl.MathEntities = MathEntities;
                        var MathExpressionEngine = (function () {
                            function MathExpressionEngine(expression) {
                                this._cacheAST = this.buildAST(this.shuntingYard(expression));
                            }
                            MathExpressionEngine.parse = function (p_expression) {
                                var newEngine = new org.mwg.ml.common.mathexp.impl.MathExpressionEngine(p_expression);
                                return newEngine;
                            };
                            MathExpressionEngine.isNumber = function (st) {
                                return !isNaN(+st);
                            };
                            MathExpressionEngine.isDigit = function (c) {
                                var cc = c.charCodeAt(0);
                                if (cc >= 0x30 && cc <= 0x39) {
                                    return true;
                                }
                                return false;
                            };
                            MathExpressionEngine.isLetter = function (c) {
                                var cc = c.charCodeAt(0);
                                if ((cc >= 0x41 && cc <= 0x5A) || (cc >= 0x61 && cc <= 0x7A)) {
                                    return true;
                                }
                                return false;
                            };
                            MathExpressionEngine.isWhitespace = function (c) {
                                var cc = c.charCodeAt(0);
                                if ((cc >= 0x0009 && cc <= 0x000D) || (cc == 0x0020) || (cc == 0x0085) || (cc == 0x00A0)) {
                                    return true;
                                }
                                return false;
                            };
                            MathExpressionEngine.prototype.shuntingYard = function (expression) {
                                var outputQueue = new java.util.ArrayList();
                                var stack = new java.util.Stack();
                                var tokenizer = new org.mwg.ml.common.mathexp.impl.MathExpressionTokenizer(expression);
                                var lastFunction = null;
                                var previousToken = null;
                                while (tokenizer.hasNext()) {
                                    var token = tokenizer.next();
                                    if (org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().functions.keySet().contains(token.toUpperCase())) {
                                        stack.push(token);
                                        lastFunction = token;
                                    }
                                    else {
                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(",", token)) {
                                            while (!stack.isEmpty() && !org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals("(", stack.peek())) {
                                                outputQueue.add(stack.pop());
                                            }
                                            if (stack.isEmpty()) {
                                                throw new Error("Parse error for function '" + lastFunction + "'");
                                            }
                                        }
                                        else {
                                            if (org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.keySet().contains(token)) {
                                                var o1 = org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.get(token);
                                                var token2 = stack.isEmpty() ? null : stack.peek();
                                                while (org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.keySet().contains(token2) && ((o1.isLeftAssoc() && o1.getPrecedence() <= org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.get(token2).getPrecedence()) || (o1.getPrecedence() < org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.get(token2).getPrecedence()))) {
                                                    outputQueue.add(stack.pop());
                                                    token2 = stack.isEmpty() ? null : stack.peek();
                                                }
                                                stack.push(token);
                                            }
                                            else {
                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals("(", token)) {
                                                    if (previousToken != null) {
                                                        if (org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isNumber(previousToken)) {
                                                            throw new Error("Missing operator at character position " + tokenizer.getPos());
                                                        }
                                                    }
                                                    stack.push(token);
                                                }
                                                else {
                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(")", token)) {
                                                        while (!stack.isEmpty() && !org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals("(", stack.peek())) {
                                                            outputQueue.add(stack.pop());
                                                        }
                                                        if (stack.isEmpty()) {
                                                            throw new Error("Mismatched parentheses");
                                                        }
                                                        stack.pop();
                                                        if (!stack.isEmpty() && org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().functions.keySet().contains(stack.peek().toUpperCase())) {
                                                            outputQueue.add(stack.pop());
                                                        }
                                                    }
                                                    else {
                                                        outputQueue.add(token);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    previousToken = token;
                                }
                                while (!stack.isEmpty()) {
                                    var element = stack.pop();
                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals("(", element) || org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(")", element)) {
                                        throw new Error("Mismatched parentheses");
                                    }
                                    outputQueue.add(element);
                                }
                                return outputQueue;
                            };
                            MathExpressionEngine.prototype.eval = function (context, variables) {
                                if (this._cacheAST == null) {
                                    throw new Error("Call parse before");
                                }
                                var stack = new java.util.Stack();
                                for (var ii = 0; ii < this._cacheAST.length; ii++) {
                                    var mathToken = this._cacheAST[ii];
                                    switch (mathToken.type()) {
                                        case 0:
                                            var v1 = stack.pop();
                                            var v2 = stack.pop();
                                            var castedOp = mathToken;
                                            stack.push(castedOp.eval(v2, v1));
                                            break;
                                        case 1:
                                            var castedFunction = mathToken;
                                            var p = new Float64Array(castedFunction.getNumParams());
                                            for (var i = castedFunction.getNumParams() - 1; i >= 0; i--) {
                                                p[i] = stack.pop();
                                            }
                                            stack.push(castedFunction.eval(p));
                                            break;
                                        case 2:
                                            var castedDouble = mathToken;
                                            stack.push(castedDouble.content());
                                            break;
                                        case 3:
                                            var castedFreeToken = mathToken;
                                            var resolvedVar = variables.get(castedFreeToken.content());
                                            if (resolvedVar != null) {
                                                stack.push(resolvedVar);
                                            }
                                            else {
                                                if (context != null) {
                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals("TIME", castedFreeToken.content())) {
                                                        stack.push(context.time());
                                                    }
                                                    else {
                                                        var tokenName = castedFreeToken.content().trim();
                                                        var resolved;
                                                        var cleanName;
                                                        if (tokenName.length > 0 && tokenName.charAt(0) == '{' && tokenName.charAt(tokenName.length - 1) == '}') {
                                                            resolved = context.get(castedFreeToken.content().substring(1, tokenName.length - 1));
                                                            cleanName = castedFreeToken.content().substring(1, tokenName.length - 1);
                                                        }
                                                        else {
                                                            resolved = context.get(castedFreeToken.content());
                                                            cleanName = castedFreeToken.content();
                                                        }
                                                        if (cleanName.length > 0 && cleanName.charAt(0) == '$') {
                                                            cleanName = cleanName.substring(1);
                                                        }
                                                        if (resolved != null) {
                                                            var resultAsDouble = this.parseDouble(resolved.toString());
                                                            variables.put(cleanName, resultAsDouble);
                                                            var valueString = resolved.toString();
                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(valueString, "true")) {
                                                                stack.push(1.0);
                                                            }
                                                            else {
                                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(valueString, "false")) {
                                                                    stack.push(0.0);
                                                                }
                                                                else {
                                                                    try {
                                                                        stack.push(resultAsDouble);
                                                                    }
                                                                    catch ($ex$) {
                                                                        if ($ex$ instanceof Error) {
                                                                            var e = $ex$;
                                                                        }
                                                                        else {
                                                                            throw $ex$;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        else {
                                                            throw new Error("Unknow variable for name " + castedFreeToken.content());
                                                        }
                                                    }
                                                }
                                                else {
                                                    throw new Error("Unknow variable for name " + castedFreeToken.content());
                                                }
                                            }
                                            break;
                                    }
                                }
                                var result = stack.pop();
                                if (result == null) {
                                    return 0;
                                }
                                else {
                                    return result;
                                }
                            };
                            MathExpressionEngine.prototype.buildAST = function (rpn) {
                                var result = new Array(rpn.size());
                                for (var ii = 0; ii < rpn.size(); ii++) {
                                    var token = rpn.get(ii);
                                    if (org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.keySet().contains(token)) {
                                        result[ii] = org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.get(token);
                                    }
                                    else {
                                        if (org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().functions.keySet().contains(token.toUpperCase())) {
                                            result[ii] = org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().functions.get(token.toUpperCase());
                                        }
                                        else {
                                            if (token.length > 0 && org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isLetter(token.charAt(0))) {
                                                result[ii] = new org.mwg.ml.common.mathexp.impl.MathFreeToken(token);
                                            }
                                            else {
                                                try {
                                                    var parsed = this.parseDouble(token);
                                                    result[ii] = new org.mwg.ml.common.mathexp.impl.MathDoubleToken(parsed);
                                                }
                                                catch ($ex$) {
                                                    if ($ex$ instanceof Error) {
                                                        var e = $ex$;
                                                        result[ii] = new org.mwg.ml.common.mathexp.impl.MathFreeToken(token);
                                                    }
                                                    else {
                                                        throw $ex$;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                return result;
                            };
                            MathExpressionEngine.prototype.parseDouble = function (val) {
                                return parseFloat(val);
                            };
                            MathExpressionEngine.decimalSeparator = '.';
                            MathExpressionEngine.minusSign = '-';
                            return MathExpressionEngine;
                        }());
                        impl.MathExpressionEngine = MathExpressionEngine;
                        var MathExpressionTokenizer = (function () {
                            function MathExpressionTokenizer(input) {
                                this.pos = 0;
                                this.input = input.trim();
                            }
                            MathExpressionTokenizer.prototype.hasNext = function () {
                                return (this.pos < this.input.length);
                            };
                            MathExpressionTokenizer.prototype.peekNextChar = function () {
                                if (this.pos < (this.input.length - 1)) {
                                    return this.input.charAt(this.pos + 1);
                                }
                                else {
                                    return '\0';
                                }
                            };
                            MathExpressionTokenizer.prototype.next = function () {
                                var token = new java.lang.StringBuilder();
                                if (this.pos >= this.input.length) {
                                    return this.previousToken = null;
                                }
                                var ch = this.input.charAt(this.pos);
                                while (org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isWhitespace(ch) && this.pos < this.input.length) {
                                    ch = this.input.charAt(++this.pos);
                                }
                                if (org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isDigit(ch)) {
                                    while ((org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isDigit(ch) || ch == org.mwg.ml.common.mathexp.impl.MathExpressionEngine.decimalSeparator) && (this.pos < this.input.length)) {
                                        token.append(this.input.charAt(this.pos++));
                                        ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                                    }
                                }
                                else {
                                    if (ch == org.mwg.ml.common.mathexp.impl.MathExpressionEngine.minusSign && org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isDigit(this.peekNextChar()) && (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals("(", this.previousToken) || org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(",", this.previousToken) || this.previousToken == null || org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.keySet().contains(this.previousToken))) {
                                        token.append(org.mwg.ml.common.mathexp.impl.MathExpressionEngine.minusSign);
                                        this.pos++;
                                        token.append(this.next());
                                    }
                                    else {
                                        if (org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isLetter(ch) || (ch == '_') || (ch == '{') || (ch == '}') || (ch == '$')) {
                                            while ((org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isLetter(ch) || org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isDigit(ch) || (ch == '_') || (ch == '{') || (ch == '}') || (ch == '$')) && (this.pos < this.input.length)) {
                                                token.append(this.input.charAt(this.pos++));
                                                ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                                            }
                                        }
                                        else {
                                            if (ch == '(' || ch == ')' || ch == ',') {
                                                token.append(ch);
                                                this.pos++;
                                            }
                                            else {
                                                while (!org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isLetter(ch) && !org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isDigit(ch) && ch != '_' && !org.mwg.ml.common.mathexp.impl.MathExpressionEngine.isWhitespace(ch) && ch != '(' && ch != ')' && ch != ',' && (ch != '{') && (ch != '}') && (ch != '$') && (this.pos < this.input.length)) {
                                                    token.append(this.input.charAt(this.pos));
                                                    this.pos++;
                                                    ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                                                    if (ch == org.mwg.ml.common.mathexp.impl.MathExpressionEngine.minusSign) {
                                                        break;
                                                    }
                                                }
                                                if (!org.mwg.ml.common.mathexp.impl.MathEntities.getINSTANCE().operators.keySet().contains(token.toString())) {
                                                    throw new Error("Unknown operator '" + token + "' at position " + (this.pos - token.length + 1));
                                                }
                                            }
                                        }
                                    }
                                }
                                return this.previousToken = token.toString();
                            };
                            MathExpressionTokenizer.prototype.getPos = function () {
                                return this.pos;
                            };
                            return MathExpressionTokenizer;
                        }());
                        impl.MathExpressionTokenizer = MathExpressionTokenizer;
                        var MathFreeToken = (function () {
                            function MathFreeToken(content) {
                                this._content = content;
                            }
                            MathFreeToken.prototype.content = function () {
                                return this._content;
                            };
                            MathFreeToken.prototype.type = function () {
                                return 3;
                            };
                            return MathFreeToken;
                        }());
                        impl.MathFreeToken = MathFreeToken;
                        var MathFunction = (function () {
                            function MathFunction(name, numParams) {
                                this.name = name.toUpperCase();
                                this.numParams = numParams;
                            }
                            MathFunction.prototype.getName = function () {
                                return this.name;
                            };
                            MathFunction.prototype.getNumParams = function () {
                                return this.numParams;
                            };
                            MathFunction.prototype.eval = function (p) {
                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "NOT")) {
                                    return (p[0] == 0) ? 1 : 0;
                                }
                                else {
                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "IF")) {
                                        return !(p[0] == 0) ? p[1] : p[2];
                                    }
                                    else {
                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "RAND")) {
                                            return Math.random();
                                        }
                                        else {
                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "SIN")) {
                                                return Math.sin(p[0]);
                                            }
                                            else {
                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "COS")) {
                                                    return Math.cos(p[0]);
                                                }
                                                else {
                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "TAN")) {
                                                        return Math.tan(p[0]);
                                                    }
                                                    else {
                                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "ASIN")) {
                                                            return Math.asin(p[0]);
                                                        }
                                                        else {
                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "ACOS")) {
                                                                return Math.acos(p[0]);
                                                            }
                                                            else {
                                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "ATAN")) {
                                                                    return Math.atan(p[0]);
                                                                }
                                                                else {
                                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "MAX")) {
                                                                        return p[0] > p[1] ? p[0] : p[1];
                                                                    }
                                                                    else {
                                                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "MIN")) {
                                                                            return p[0] < p[1] ? p[0] : p[1];
                                                                        }
                                                                        else {
                                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "ABS")) {
                                                                                return Math.abs(p[0]);
                                                                            }
                                                                            else {
                                                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "LOG")) {
                                                                                    return Math.log(p[0]);
                                                                                }
                                                                                else {
                                                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "ROUND")) {
                                                                                        var factor = Math.pow(10, p[1]);
                                                                                        var value = p[0] * factor;
                                                                                        var tmp = Math.round(value);
                                                                                        return tmp / factor;
                                                                                    }
                                                                                    else {
                                                                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "FLOOR")) {
                                                                                            return Math.floor(p[0]);
                                                                                        }
                                                                                        else {
                                                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "CEILING")) {
                                                                                                return Math.ceil(p[0]);
                                                                                            }
                                                                                            else {
                                                                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "SQRT")) {
                                                                                                    return Math.sqrt(p[0]);
                                                                                                }
                                                                                                else {
                                                                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "SECONDS")) {
                                                                                                        return this.date_to_seconds(p[0]);
                                                                                                    }
                                                                                                    else {
                                                                                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "MINUTES")) {
                                                                                                            return this.date_to_minutes(p[0]);
                                                                                                        }
                                                                                                        else {
                                                                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "HOURS")) {
                                                                                                                return this.date_to_hours(p[0]);
                                                                                                            }
                                                                                                            else {
                                                                                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "DAY")) {
                                                                                                                    return this.date_to_days(p[0]);
                                                                                                                }
                                                                                                                else {
                                                                                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "MONTH")) {
                                                                                                                        return this.date_to_months(p[0]);
                                                                                                                    }
                                                                                                                    else {
                                                                                                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "YEAR")) {
                                                                                                                            return this.date_to_year(p[0]);
                                                                                                                        }
                                                                                                                        else {
                                                                                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.name, "DAYOFWEEK")) {
                                                                                                                                return this.date_to_dayofweek(p[0]);
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                return 0;
                            };
                            MathFunction.prototype.date_to_seconds = function (value) {
                                var date = new Date(value);
                                return date.getSeconds();
                            };
                            MathFunction.prototype.date_to_minutes = function (value) {
                                var date = new Date(value);
                                return date.getMinutes();
                            };
                            MathFunction.prototype.date_to_hours = function (value) {
                                var date = new Date(value);
                                return date.getHours();
                            };
                            MathFunction.prototype.date_to_days = function (value) {
                                var date = new Date(value);
                                return date.getDate();
                            };
                            MathFunction.prototype.date_to_months = function (value) {
                                var date = new Date(value);
                                return date.getMonth();
                            };
                            MathFunction.prototype.date_to_year = function (value) {
                                var date = new Date(value);
                                return date.getFullYear();
                            };
                            MathFunction.prototype.date_to_dayofweek = function (value) {
                                var date = new Date(value);
                                return date.getDay();
                            };
                            MathFunction.prototype.type = function () {
                                return 1;
                            };
                            return MathFunction;
                        }());
                        impl.MathFunction = MathFunction;
                        var MathOperation = (function () {
                            function MathOperation(oper, precedence, leftAssoc) {
                                this.oper = oper;
                                this.precedence = precedence;
                                this.leftAssoc = leftAssoc;
                            }
                            MathOperation.prototype.getOper = function () {
                                return this.oper;
                            };
                            MathOperation.prototype.getPrecedence = function () {
                                return this.precedence;
                            };
                            MathOperation.prototype.isLeftAssoc = function () {
                                return this.leftAssoc;
                            };
                            MathOperation.prototype.eval = function (v1, v2) {
                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "+")) {
                                    return v1 + v2;
                                }
                                else {
                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "-")) {
                                        return v1 - v2;
                                    }
                                    else {
                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "*")) {
                                            return v1 * v2;
                                        }
                                        else {
                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "/")) {
                                                return v1 / v2;
                                            }
                                            else {
                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "%")) {
                                                    return v1 % v2;
                                                }
                                                else {
                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "^")) {
                                                        return Math.pow(v1, v2);
                                                    }
                                                    else {
                                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "&&")) {
                                                            var b1 = !(v1 == 0);
                                                            var b2 = !(v2 == 0);
                                                            return b1 && b2 ? 1 : 0;
                                                        }
                                                        else {
                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "||")) {
                                                                var b1 = !(v1 == 0);
                                                                var b2 = !(v2 == 0);
                                                                return b1 || b2 ? 1 : 0;
                                                            }
                                                            else {
                                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, ">")) {
                                                                    return v1 > v2 ? 1 : 0;
                                                                }
                                                                else {
                                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, ">=")) {
                                                                        return v1 >= v2 ? 1 : 0;
                                                                    }
                                                                    else {
                                                                        if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "<")) {
                                                                            return v1 < v2 ? 1 : 0;
                                                                        }
                                                                        else {
                                                                            if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "<=")) {
                                                                                return v1 <= v2 ? 1 : 0;
                                                                            }
                                                                            else {
                                                                                if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "==")) {
                                                                                    return v1 == v2 ? 1 : 0;
                                                                                }
                                                                                else {
                                                                                    if (org.mwg.ml.common.mathexp.impl.PrimitiveHelper.equals(this.oper, "!=")) {
                                                                                        return v1 != v2 ? 1 : 0;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                return 0;
                            };
                            MathOperation.prototype.type = function () {
                                return 0;
                            };
                            return MathOperation;
                        }());
                        impl.MathOperation = MathOperation;
                        var PrimitiveHelper = (function () {
                            function PrimitiveHelper() {
                            }
                            PrimitiveHelper.equals = function (src, other) {
                                return src === other;
                            };
                            return PrimitiveHelper;
                        }());
                        impl.PrimitiveHelper = PrimitiveHelper;
                    })(impl = mathexp.impl || (mathexp.impl = {}));
                })(mathexp = common.mathexp || (common.mathexp = {}));
                var matrix;
                (function (matrix) {
                    var blassolver;
                    (function (blassolver) {
                        var blas;
                        (function (blas) {
                            var BlasHelper = (function () {
                                function BlasHelper() {
                                }
                                BlasHelper.transTypeToChar = function (type) {
                                    if (type.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                        return org.mwg.ml.common.matrix.blassolver.blas.BlasHelper.TRANSPOSE_TYPE_NOTRANSPOSE;
                                    }
                                    else {
                                        if (type.equals(org.mwg.ml.common.matrix.TransposeType.TRANSPOSE)) {
                                            return org.mwg.ml.common.matrix.blassolver.blas.BlasHelper.TRANSPOSE_TYPE_TRANSPOSE;
                                        }
                                    }
                                    return null;
                                };
                                BlasHelper.TRANSPOSE_TYPE_CONJUCATE = "c";
                                BlasHelper.TRANSPOSE_TYPE_NOTRANSPOSE = "n";
                                BlasHelper.TRANSPOSE_TYPE_TRANSPOSE = "t";
                                return BlasHelper;
                            }());
                            blas.BlasHelper = BlasHelper;
                        })(blas = blassolver.blas || (blassolver.blas = {}));
                        var BlasMatrixEngine = (function () {
                            function BlasMatrixEngine() {
                                this._blas = new org.mwg.ml.common.matrix.blassolver.blas.JSBlas();
                            }
                            BlasMatrixEngine.prototype.setBlas = function (p_blas) {
                                this._blas = p_blas;
                            };
                            BlasMatrixEngine.prototype.getBlas = function () {
                                return this._blas;
                            };
                            BlasMatrixEngine.prototype.multiplyTransposeAlphaBeta = function (transA, alpha, matA, transB, beta, matB) {
                                if (org.mwg.ml.common.matrix.Matrix.testDimensionsAB(transA, transB, matA, matB)) {
                                    var k = 0;
                                    var dimC = new Int32Array(2);
                                    if (transA.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                        k = matA.columns();
                                        if (transB.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                            dimC[0] = matA.rows();
                                            dimC[1] = matB.columns();
                                        }
                                        else {
                                            dimC[0] = matA.rows();
                                            dimC[1] = matB.rows();
                                        }
                                    }
                                    else {
                                        k = matA.rows();
                                        if (transB.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                            dimC[0] = matA.columns();
                                            dimC[1] = matB.columns();
                                        }
                                        else {
                                            dimC[0] = matA.columns();
                                            dimC[1] = matB.rows();
                                        }
                                    }
                                    var matC = new org.mwg.ml.common.matrix.Matrix(null, dimC[0], dimC[1]);
                                    this._blas.dgemm(transA, transB, matC.rows(), matC.columns(), k, alpha, matA.data(), 0, matA.rows(), matB.data(), 0, matB.rows(), beta, matC.data(), 0, matC.rows());
                                    return matC;
                                }
                                else {
                                    throw new Error("Dimensions mismatch between A,B and C");
                                }
                            };
                            BlasMatrixEngine.prototype.invert = function (mat, invertInPlace) {
                                if (mat.rows() != mat.columns()) {
                                    return null;
                                }
                                if (invertInPlace) {
                                    var dlu = new org.mwg.ml.common.matrix.blassolver.LU(mat.rows(), mat.columns(), this._blas);
                                    if (dlu.invert(mat)) {
                                        return mat;
                                    }
                                    else {
                                        return null;
                                    }
                                }
                                else {
                                    var result = new org.mwg.ml.common.matrix.Matrix(null, mat.rows(), mat.columns());
                                    var A_temp = new org.mwg.ml.common.matrix.Matrix(null, mat.rows(), mat.columns());
                                    java.lang.System.arraycopy(mat.data(), 0, A_temp.data(), 0, mat.columns() * mat.rows());
                                    var dlu = new org.mwg.ml.common.matrix.blassolver.LU(A_temp.rows(), A_temp.columns(), this._blas);
                                    if (dlu.invert(A_temp)) {
                                        result.setData(A_temp.data());
                                        return result;
                                    }
                                    else {
                                        return null;
                                    }
                                }
                            };
                            BlasMatrixEngine.prototype.pinv = function (mat, invertInPlace) {
                                return this.solve(mat, org.mwg.ml.common.matrix.Matrix.identity(mat.rows(), mat.rows()));
                            };
                            BlasMatrixEngine.prototype.solveQR = function (matA, matB, workInPlace, transB) {
                                var solver = org.mwg.ml.common.matrix.blassolver.QR.factorize(matA, workInPlace, this._blas);
                                var coef = new org.mwg.ml.common.matrix.Matrix(null, matA.columns(), matB.columns());
                                if (transB != org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE) {
                                    matB = org.mwg.ml.common.matrix.Matrix.transpose(matB);
                                }
                                solver.solve(matB, coef);
                                return coef;
                            };
                            BlasMatrixEngine.prototype.decomposeSVD = function (matA, workInPlace) {
                                var svd = new org.mwg.ml.common.matrix.blassolver.SVD(matA.rows(), matA.columns(), this._blas);
                                svd.factor(matA, workInPlace);
                                return svd;
                            };
                            BlasMatrixEngine.prototype.solveLU = function (matA, matB, workInPlace, transB) {
                                if (!workInPlace) {
                                    var A_temp = new org.mwg.ml.common.matrix.Matrix(null, matA.rows(), matA.columns());
                                    java.lang.System.arraycopy(matA.data(), 0, A_temp.data(), 0, matA.columns() * matA.rows());
                                    var dlu = new org.mwg.ml.common.matrix.blassolver.LU(A_temp.rows(), A_temp.columns(), this._blas);
                                    dlu.factor(A_temp, true);
                                    if (dlu.isSingular()) {
                                        return null;
                                    }
                                    var B_temp = new org.mwg.ml.common.matrix.Matrix(null, matB.rows(), matB.columns());
                                    java.lang.System.arraycopy(matB.data(), 0, B_temp.data(), 0, matB.columns() * matB.rows());
                                    dlu.transSolve(B_temp, transB);
                                    return B_temp;
                                }
                                else {
                                    var dlu = new org.mwg.ml.common.matrix.blassolver.LU(matA.rows(), matA.columns(), this._blas);
                                    dlu.factor(matA, true);
                                    if (dlu.isSingular()) {
                                        return null;
                                    }
                                    dlu.transSolve(matB, transB);
                                    return matB;
                                }
                            };
                            BlasMatrixEngine.prototype.solve = function (A, B) {
                                return (A.rows() == A.columns() ? (new org.mwg.ml.common.matrix.blassolver.LU(A.rows(), A.columns(), this._blas).factor(A, false)).solve(B) : this.solveQR(A, B, false, org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE));
                            };
                            return BlasMatrixEngine;
                        }());
                        blassolver.BlasMatrixEngine = BlasMatrixEngine;
                        var JobSVD = (function () {
                            function JobSVD() {
                            }
                            JobSVD.prototype.netlib = function () {
                                switch (this) {
                                    case org.mwg.ml.common.matrix.blassolver.JobSVD.All:
                                        return "A";
                                    case org.mwg.ml.common.matrix.blassolver.JobSVD.Part:
                                        return "S";
                                    case org.mwg.ml.common.matrix.blassolver.JobSVD.Overwrite:
                                        return "O";
                                    default:
                                        return "N";
                                }
                            };
                            JobSVD.prototype.equals = function (other) {
                                return this == other;
                            };
                            JobSVD.values = function () {
                                return JobSVD._JobSVDVALUES;
                            };
                            JobSVD.All = new JobSVD();
                            JobSVD.None = new JobSVD();
                            JobSVD.Overwrite = new JobSVD();
                            JobSVD.Part = new JobSVD();
                            JobSVD._JobSVDVALUES = [
                                JobSVD.All,
                                JobSVD.None,
                                JobSVD.Overwrite,
                                JobSVD.Part
                            ];
                            return JobSVD;
                        }());
                        blassolver.JobSVD = JobSVD;
                        var LU = (function () {
                            function LU(m, n, blas) {
                                this._blas = blas;
                                this.LU = new org.mwg.ml.common.matrix.Matrix(null, m, n);
                                this.piv = new Int32Array(Math.min(m, n));
                            }
                            LU.prototype.getLU = function () {
                                return this.LU;
                            };
                            LU.factorize = function (A, blas) {
                                return new org.mwg.ml.common.matrix.blassolver.LU(A.rows(), A.columns(), blas).factor(A, false);
                            };
                            LU.prototype.factor = function (A, factorInPlace) {
                                if (factorInPlace) {
                                    this.singular = false;
                                    var info = new Int32Array(1);
                                    info[0] = 0;
                                    this._blas.dgetrf(A.rows(), A.columns(), A.data(), 0, A.rows(), this.piv, 0, info);
                                    if (info[0] > 0) {
                                        this.singular = true;
                                    }
                                    else {
                                        if (info[0] < 0) {
                                            throw new Error();
                                        }
                                    }
                                    this.LU.setData(A.data());
                                    return this;
                                }
                                else {
                                    this.singular = false;
                                    var B = A.clone();
                                    var info = new Int32Array(1);
                                    info[0] = 0;
                                    this._blas.dgetrf(B.rows(), B.columns(), B.data(), 0, B.rows(), this.piv, 0, info);
                                    if (info[0] > 0) {
                                        this.singular = true;
                                    }
                                    else {
                                        if (info[0] < 0) {
                                            throw new Error();
                                        }
                                    }
                                    this.LU.setData(B.data());
                                    return this;
                                }
                            };
                            LU.prototype.getL = function () {
                                var numRows = this.LU.rows();
                                var numCols = this.LU.rows() < this.LU.columns() ? this.LU.rows() : this.LU.columns();
                                var lower = new org.mwg.ml.common.matrix.Matrix(null, numRows, numCols);
                                for (var i = 0; i < numCols; i++) {
                                    lower.set(i, i, 1.0);
                                    for (var j = 0; j < i; j++) {
                                        lower.set(i, j, this.LU.get(i, j));
                                    }
                                }
                                if (numRows > numCols) {
                                    for (var i = numCols; i < numRows; i++) {
                                        for (var j = 0; j < numCols; j++) {
                                            lower.set(i, j, this.LU.get(i, j));
                                        }
                                    }
                                }
                                return lower;
                            };
                            LU.prototype.getU = function () {
                                var numRows = this.LU.rows() < this.LU.columns() ? this.LU.rows() : this.LU.columns();
                                var numCols = this.LU.columns();
                                var upper = new org.mwg.ml.common.matrix.Matrix(null, numRows, numCols);
                                for (var i = 0; i < numRows; i++) {
                                    for (var j = i; j < numCols; j++) {
                                        upper.set(i, j, this.LU.get(i, j));
                                    }
                                }
                                return upper;
                            };
                            LU.prototype.getPivots = function () {
                                return this.piv;
                            };
                            LU.prototype.isSingular = function () {
                                return this.singular;
                            };
                            LU.prototype.solve = function (B) {
                                return this.transSolve(B, org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE);
                            };
                            LU.prototype.transSolve = function (B, trans) {
                                if (B.rows() != this.LU.rows()) {
                                    throw new Error("B.numRows() != LU.numRows()");
                                }
                                var info = new Int32Array(1);
                                this._blas.dgetrs(trans, this.LU.rows(), B.columns(), this.LU.data(), 0, this.LU.rows(), this.piv, 0, B.data(), 0, B.rows(), info);
                                if (info[0] < 0) {
                                    throw new Error();
                                }
                                return B;
                            };
                            LU.prototype.invert = function (A) {
                                var info = new Int32Array(1);
                                info[0] = 0;
                                this._blas.dgetrf(A.rows(), A.columns(), A.data(), 0, A.rows(), this.piv, 0, info);
                                if (info[0] > 0) {
                                    this.singular = true;
                                    return false;
                                }
                                else {
                                    if (info[0] < 0) {
                                        throw new Error();
                                    }
                                }
                                var lwork = A.rows() * A.rows();
                                var work = new Float64Array(lwork);
                                for (var i = 0; i < lwork; i++) {
                                    work[i] = 0;
                                }
                                this._blas.dgetri(A.rows(), A.data(), 0, A.rows(), this.piv, 0, work, 0, lwork, info);
                                if (info[0] != 0) {
                                    return false;
                                }
                                else {
                                    return true;
                                }
                            };
                            return LU;
                        }());
                        blassolver.LU = LU;
                        var QR = (function () {
                            function QR(rows, columns, blas) {
                                this._blas = blas;
                                if (columns > rows) {
                                    throw new Error("n > m");
                                }
                                this.m = rows;
                                this.n = columns;
                                this.k = Math.min(this.m, this.n);
                                this.tau = new Float64Array(this.k);
                                this.R = new org.mwg.ml.common.matrix.Matrix(null, this.n, this.n);
                            }
                            QR.factorize = function (A, workInPlace, blas) {
                                return new org.mwg.ml.common.matrix.blassolver.QR(A.rows(), A.columns(), blas).factor(A, workInPlace);
                            };
                            QR.prototype.factor = function (matA, workInPlace) {
                                var A;
                                if (!workInPlace) {
                                    A = matA.clone();
                                }
                                else {
                                    A = matA;
                                }
                                var lwork;
                                this.work = new Float64Array(1);
                                var info = new Int32Array(1);
                                info[0] = 0;
                                this._blas.dgeqrf(this.m, this.n, new Float64Array(0), 0, this.m, new Float64Array(0), 0, this.work, 0, -1, info);
                                if (info[0] != 0) {
                                    lwork = this.n;
                                }
                                else {
                                    lwork = this.work[0];
                                }
                                lwork = Math.max(1, lwork);
                                this.work = new Float64Array(lwork);
                                this.workGen = new Float64Array(1);
                                var info = new Int32Array(1);
                                info[0] = 0;
                                this._blas.dorgqr(this.m, this.n, this.k, new Float64Array(0), 0, this.m, new Float64Array(0), 0, this.workGen, 0, -1, info);
                                if (info[0] != 0) {
                                    lwork = this.n;
                                }
                                else {
                                    lwork = this.workGen[0];
                                }
                                lwork = Math.max(1, lwork);
                                this.workGen = new Float64Array(lwork);
                                var info = new Int32Array(1);
                                info[0] = 0;
                                this._blas.dgeqrf(this.m, this.n, A.data(), 0, this.m, this.tau, 0, this.work, 0, this.work.length, info);
                                if (info[0] < 0) {
                                    throw new Error("" + info[0]);
                                }
                                for (var col = 0; col < A.columns(); col++) {
                                    for (var row = 0; row <= col; row++) {
                                        this.R.set(row, col, A.get(row, col));
                                    }
                                }
                                info[0] = 0;
                                this._blas.dorgqr(this.m, this.n, this.k, A.data(), 0, this.m, this.tau, 0, this.workGen, 0, this.workGen.length, info);
                                if (info[0] < 0) {
                                    throw new Error();
                                }
                                this.Q = A;
                                return this;
                            };
                            QR.prototype.solve = function (B, X) {
                                var BnumCols = B.columns();
                                var Y = new org.mwg.ml.common.matrix.Matrix(null, this.m, 1);
                                var Z;
                                for (var colB = 0; colB < BnumCols; colB++) {
                                    for (var i = 0; i < this.m; i++) {
                                        Y.setAtIndex(i, B.get(i, colB));
                                    }
                                    Z = org.mwg.ml.common.matrix.Matrix.multiplyTransposeAlphaBeta(org.mwg.ml.common.matrix.TransposeType.TRANSPOSE, 1.0, this.Q, org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE, 1.0, Y);
                                    this.solveU(this.R, Z.data(), this.n, this.m);
                                    for (var i = 0; i < this.n; i++) {
                                        X.set(i, colB, Z.getAtIndex(i));
                                    }
                                }
                            };
                            QR.prototype.solveU = function (U, b, n, m) {
                                for (var i = n - 1; i >= 0; i--) {
                                    var sum = b[i];
                                    for (var j = i + 1; j < n; j++) {
                                        sum -= U.get(i, j) * b[j];
                                    }
                                    b[i] = sum / U.get(i, i);
                                }
                            };
                            QR.prototype.getR = function () {
                                return this.R;
                            };
                            QR.prototype.getQ = function () {
                                return this.Q;
                            };
                            return QR;
                        }());
                        blassolver.QR = QR;
                        var SVD = (function () {
                            function SVD(m, n, blas) {
                                this.m = m;
                                this.n = n;
                                this._blas = blas;
                                this.vectors = true;
                                this.S = new Float64Array(Math.min(m, n));
                                if (this.vectors) {
                                    this.U = new org.mwg.ml.common.matrix.Matrix(null, m, m);
                                    this.Vt = new org.mwg.ml.common.matrix.Matrix(null, n, n);
                                }
                                else {
                                    this.U = this.Vt = null;
                                }
                                this.job = this.vectors ? org.mwg.ml.common.matrix.blassolver.JobSVD.All : org.mwg.ml.common.matrix.blassolver.JobSVD.None;
                                this.iwork = new Int32Array(8 * Math.min(m, n));
                                var worksize = new Float64Array(1);
                                var info = new Int32Array(1);
                                this._blas.dgesdd(this.job.netlib(), m, n, new Float64Array(0), Math.max(1, m), new Float64Array(0), new Float64Array(0), Math.max(1, m), new Float64Array(0), Math.max(1, n), worksize, -1, this.iwork, info);
                                var lwork = -1;
                                if (info[0] != 0) {
                                    if (this.vectors) {
                                        lwork = 3 * Math.min(m, n) * Math.min(m, n) + Math.max(Math.max(m, n), 4 * Math.min(m, n) * Math.min(m, n) + 4 * Math.min(m, n));
                                    }
                                    else {
                                        lwork = 3 * Math.min(m, n) * Math.min(m, n) + Math.max(Math.max(m, n), 5 * Math.min(m, n) * Math.min(m, n) + 4 * Math.min(m, n));
                                    }
                                }
                                else {
                                    lwork = worksize[0];
                                }
                                lwork = Math.max(lwork, 1);
                                this.work = new Float64Array(lwork);
                            }
                            SVD.prototype.factor = function (A, workInPlace) {
                                if (A.rows() != this.m) {
                                    throw new Error("A.numRows() != m");
                                }
                                else {
                                    if (A.columns() != this.n) {
                                        throw new Error("A.numColumns() != n");
                                    }
                                }
                                var info = new Int32Array(1);
                                info[0] = 0;
                                if (workInPlace) {
                                    this._blas.dgesdd(this.job.netlib(), this.m, this.n, A.data(), Math.max(1, this.m), this.S, this.vectors ? this.U.data() : new Float64Array(0), Math.max(1, this.m), this.vectors ? this.Vt.data() : new Float64Array(0), Math.max(1, this.n), this.work, this.work.length, this.iwork, info);
                                }
                                else {
                                    var Adata = A.data();
                                    var cloned = new Float64Array(Adata.length);
                                    java.lang.System.arraycopy(Adata, 0, cloned, 0, Adata.length);
                                    this._blas.dgesdd(this.job.netlib(), this.m, this.n, cloned, Math.max(1, this.m), this.S, this.vectors ? this.U.data() : new Float64Array(0), Math.max(1, this.m), this.vectors ? this.Vt.data() : new Float64Array(0), Math.max(1, this.n), this.work, this.work.length, this.iwork, info);
                                }
                                if (info[0] > 0) {
                                    throw new Error("NotConvergedException.Reason.Iterations");
                                }
                                else {
                                    if (info[0] < 0) {
                                        throw new Error();
                                    }
                                }
                                return this;
                            };
                            SVD.prototype.hasSingularVectors = function () {
                                return this.U != null;
                            };
                            SVD.prototype.getU = function () {
                                return this.U;
                            };
                            SVD.prototype.getVt = function () {
                                return this.Vt;
                            };
                            SVD.prototype.getS = function () {
                                return this.S;
                            };
                            SVD.prototype.getSMatrix = function () {
                                var matS = new org.mwg.ml.common.matrix.Matrix(null, this.m, this.n);
                                for (var i = 0; i < this.S.length; i++) {
                                    matS.set(i, i, this.S[i]);
                                }
                                return matS;
                            };
                            return SVD;
                        }());
                        blassolver.SVD = SVD;
                    })(blassolver = matrix.blassolver || (matrix.blassolver = {}));
                    var jamasolver;
                    (function (jamasolver) {
                        var JamaMatrixEngine = (function () {
                            function JamaMatrixEngine() {
                            }
                            JamaMatrixEngine.prototype.multiplyTransposeAlphaBeta = function (transA, alpha, matA, transB, beta, matB) {
                                if (org.mwg.ml.common.matrix.Matrix.testDimensionsAB(transA, transB, matA, matB)) {
                                    var dimC = new Int32Array(3);
                                    if (transA.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                        if (transB.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                            dimC[0] = matA.rows();
                                            dimC[1] = matB.columns();
                                            dimC[2] = matA.columns();
                                        }
                                        else {
                                            dimC[0] = matA.rows();
                                            dimC[1] = matB.rows();
                                            dimC[2] = matA.columns();
                                        }
                                    }
                                    else {
                                        if (transB.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                            dimC[0] = matA.columns();
                                            dimC[1] = matB.columns();
                                            dimC[2] = matA.rows();
                                        }
                                        else {
                                            dimC[0] = matA.columns();
                                            dimC[1] = matB.rows();
                                            dimC[2] = matA.rows();
                                        }
                                    }
                                    var matC = new org.mwg.ml.common.matrix.Matrix(null, dimC[0], dimC[1]);
                                    if (transA == org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE && transB == org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE) {
                                        for (var i = 0; i < dimC[0]; i++) {
                                            for (var j = 0; j < dimC[1]; j++) {
                                                for (var k = 0; k < dimC[2]; k++) {
                                                    matC.add(i, j, alpha * matA.get(i, k) * beta * matB.get(k, j));
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        if (transA == org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE && transB == org.mwg.ml.common.matrix.TransposeType.TRANSPOSE) {
                                            for (var i = 0; i < dimC[0]; i++) {
                                                for (var j = 0; j < dimC[1]; j++) {
                                                    for (var k = 0; k < dimC[2]; k++) {
                                                        matC.add(i, j, alpha * matA.get(i, k) * beta * matB.get(j, k));
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            if (transA == org.mwg.ml.common.matrix.TransposeType.TRANSPOSE && transB == org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE) {
                                                for (var i = 0; i < dimC[0]; i++) {
                                                    for (var j = 0; j < dimC[1]; j++) {
                                                        for (var k = 0; k < dimC[2]; k++) {
                                                            matC.add(i, j, alpha * matA.get(k, i) * beta * matB.get(k, j));
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                if (transA == org.mwg.ml.common.matrix.TransposeType.TRANSPOSE && transB == org.mwg.ml.common.matrix.TransposeType.TRANSPOSE) {
                                                    for (var i = 0; i < dimC[0]; i++) {
                                                        for (var j = 0; j < dimC[1]; j++) {
                                                            for (var k = 0; k < dimC[2]; k++) {
                                                                matC.add(i, j, alpha * matA.get(k, i) * beta * matB.get(j, k));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    return matC;
                                }
                                else {
                                    throw new Error("Dimensions mismatch between A,B and C");
                                }
                            };
                            JamaMatrixEngine.prototype.invert = function (mat, invertInPlace) {
                                return org.mwg.ml.common.matrix.jamasolver.JamaMatrixEngine.solve(mat, org.mwg.ml.common.matrix.Matrix.identity(mat.rows(), mat.rows()));
                            };
                            JamaMatrixEngine.prototype.pinv = function (mat, invertInPlace) {
                                return org.mwg.ml.common.matrix.jamasolver.JamaMatrixEngine.solve(mat, org.mwg.ml.common.matrix.Matrix.identity(mat.rows(), mat.rows()));
                            };
                            JamaMatrixEngine.prototype.solveLU = function (matA, matB, workInPlace, transB) {
                                var btem;
                                if (transB == org.mwg.ml.common.matrix.TransposeType.TRANSPOSE) {
                                    btem = org.mwg.ml.common.matrix.Matrix.transpose(matB);
                                }
                                else {
                                    btem = matB;
                                }
                                return (new org.mwg.ml.common.matrix.jamasolver.LU(matA)).solve(btem);
                            };
                            JamaMatrixEngine.prototype.solveQR = function (matA, matB, workInPlace, transB) {
                                var btem;
                                if (transB == org.mwg.ml.common.matrix.TransposeType.TRANSPOSE) {
                                    btem = org.mwg.ml.common.matrix.Matrix.transpose(matB);
                                }
                                else {
                                    btem = matB;
                                }
                                return (new org.mwg.ml.common.matrix.jamasolver.QR(matA)).solve(btem);
                            };
                            JamaMatrixEngine.prototype.decomposeSVD = function (matA, workInPlace) {
                                return new org.mwg.ml.common.matrix.jamasolver.SVD(matA);
                            };
                            JamaMatrixEngine.solve = function (A, B) {
                                return (A.rows() == A.columns() ? (new org.mwg.ml.common.matrix.jamasolver.LU(A)).solve(B) : (new org.mwg.ml.common.matrix.jamasolver.QR(A)).solve(B));
                            };
                            return JamaMatrixEngine;
                        }());
                        jamasolver.JamaMatrixEngine = JamaMatrixEngine;
                        var LU = (function () {
                            function LU(A) {
                                this.LU = A.clone();
                                this.m = A.rows();
                                this.n = A.columns();
                                this.piv = new Int32Array(this.m);
                                for (var i = 0; i < this.m; i++) {
                                    this.piv[i] = i;
                                }
                                this.pivsign = 1;
                                var LUcolj = new Float64Array(this.m);
                                for (var j = 0; j < this.n; j++) {
                                    for (var i = 0; i < this.m; i++) {
                                        LUcolj[i] = this.LU.get(i, j);
                                    }
                                    for (var i = 0; i < this.m; i++) {
                                        var kmax = Math.min(i, j);
                                        var s = 0.0;
                                        for (var k = 0; k < kmax; k++) {
                                            s += this.LU.get(i, k) * LUcolj[k];
                                        }
                                        LUcolj[i] -= s;
                                        this.LU.set(i, j, LUcolj[i]);
                                    }
                                    var p = j;
                                    for (var i = j + 1; i < this.m; i++) {
                                        if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
                                            p = i;
                                        }
                                    }
                                    if (p != j) {
                                        for (var k = 0; k < this.n; k++) {
                                            var t = this.LU.get(p, k);
                                            this.LU.set(p, k, this.LU.get(j, k));
                                            this.LU.set(j, k, t);
                                        }
                                        var k = this.piv[p];
                                        this.piv[p] = this.piv[j];
                                        this.piv[j] = k;
                                        this.pivsign = -this.pivsign;
                                    }
                                    if (j < this.m && this.LU.get(j, j) != 0.0) {
                                        for (var i = j + 1; i < this.m; i++) {
                                            this.LU.set(i, j, this.LU.get(i, j) / this.LU.get(j, j));
                                        }
                                    }
                                }
                            }
                            LU.prototype.isNonsingular = function () {
                                for (var j = 0; j < this.n; j++) {
                                    if (this.LU.get(j, j) == 0) {
                                        return false;
                                    }
                                }
                                return true;
                            };
                            LU.prototype.getL = function () {
                                var L = new org.mwg.ml.common.matrix.Matrix(null, this.m, this.n);
                                for (var i = 0; i < this.m; i++) {
                                    for (var j = 0; j < this.n; j++) {
                                        if (i > j) {
                                            L.set(i, j, this.LU.get(i, j));
                                        }
                                        else {
                                            if (i == j) {
                                                L.set(i, j, 1.0);
                                            }
                                            else {
                                                L.set(i, j, 0.0);
                                            }
                                        }
                                    }
                                }
                                return L;
                            };
                            LU.prototype.getU = function () {
                                var U = new org.mwg.ml.common.matrix.Matrix(null, this.n, this.n);
                                for (var i = 0; i < this.n; i++) {
                                    for (var j = 0; j < this.n; j++) {
                                        if (i <= j) {
                                            U.set(i, j, this.LU.get(i, j));
                                        }
                                        else {
                                            U.set(i, j, 0.0);
                                        }
                                    }
                                }
                                return U;
                            };
                            LU.prototype.getPivot = function () {
                                var p = new Int32Array(this.m);
                                for (var i = 0; i < this.m; i++) {
                                    p[i] = this.piv[i];
                                }
                                return p;
                            };
                            LU.prototype.getDoublePivot = function () {
                                var vals = new Float64Array(this.m);
                                for (var i = 0; i < this.m; i++) {
                                    vals[i] = this.piv[i];
                                }
                                return vals;
                            };
                            LU.prototype.det = function () {
                                if (this.m != this.n) {
                                    throw new Error("Matrix must be square.");
                                }
                                var d = this.pivsign;
                                for (var j = 0; j < this.n; j++) {
                                    d *= this.LU.get(j, j);
                                }
                                return d;
                            };
                            LU.prototype.solve = function (B) {
                                if (B.rows() != this.m) {
                                    throw new Error("Matrix row dimensions must agree.");
                                }
                                if (!this.isNonsingular()) {
                                    throw new Error("Matrix is singular.");
                                }
                                var nx = B.columns();
                                var X = this.getMatrix(B, this.piv, 0, nx - 1);
                                for (var k = 0; k < this.n; k++) {
                                    for (var i = k + 1; i < this.n; i++) {
                                        for (var j = 0; j < nx; j++) {
                                            X.add(i, j, -X.get(k, j) * this.LU.get(i, k));
                                        }
                                    }
                                }
                                for (var k = this.n - 1; k >= 0; k--) {
                                    for (var j = 0; j < nx; j++) {
                                        X.set(k, j, X.get(k, j) / this.LU.get(k, k));
                                    }
                                    for (var i = 0; i < k; i++) {
                                        for (var j = 0; j < nx; j++) {
                                            X.add(i, j, -X.get(k, j) * this.LU.get(i, k));
                                        }
                                    }
                                }
                                return X;
                            };
                            LU.prototype.getMatrix = function (A, r, j0, j1) {
                                var B = new org.mwg.ml.common.matrix.Matrix(null, r.length, j1 - j0 + 1);
                                try {
                                    for (var i = 0; i < r.length; i++) {
                                        for (var j = j0; j <= j1; j++) {
                                            B.set(i, j - j0, A.get(r[i], j));
                                        }
                                    }
                                }
                                catch ($ex$) {
                                    if ($ex$ instanceof Error) {
                                        var e = $ex$;
                                        throw new Error("Submatrix indices");
                                    }
                                    else {
                                        throw $ex$;
                                    }
                                }
                                return B;
                            };
                            return LU;
                        }());
                        jamasolver.LU = LU;
                        var QR = (function () {
                            function QR(A) {
                                this.QR = A.clone();
                                this.m = A.rows();
                                this.n = A.columns();
                                this.Rdiag = new Float64Array(this.n);
                                for (var k = 0; k < this.n; k++) {
                                    var nrm = 0;
                                    for (var i = k; i < this.m; i++) {
                                        nrm = org.mwg.ml.common.matrix.jamasolver.Utils.hypot(nrm, this.QR.get(i, k));
                                    }
                                    if (nrm != 0.0) {
                                        if (this.QR.get(k, k) < 0) {
                                            nrm = -nrm;
                                        }
                                        for (var i = k; i < this.m; i++) {
                                            this.QR.set(i, k, this.QR.get(i, k) / nrm);
                                        }
                                        this.QR.add(k, k, 1.0);
                                        for (var j = k + 1; j < this.n; j++) {
                                            var s = 0.0;
                                            for (var i = k; i < this.m; i++) {
                                                s += this.QR.get(i, k) * this.QR.get(i, j);
                                            }
                                            s = -s / this.QR.get(k, k);
                                            for (var i = k; i < this.m; i++) {
                                                this.QR.add(i, j, s * this.QR.get(i, k));
                                            }
                                        }
                                    }
                                    this.Rdiag[k] = -nrm;
                                }
                            }
                            QR.prototype.isFullRank = function () {
                                for (var j = 0; j < this.n; j++) {
                                    if (this.Rdiag[j] == 0) {
                                        return false;
                                    }
                                }
                                return true;
                            };
                            QR.prototype.getH = function () {
                                var H = new org.mwg.ml.common.matrix.Matrix(null, this.m, this.n);
                                for (var i = 0; i < this.m; i++) {
                                    for (var j = 0; j < this.n; j++) {
                                        if (i >= j) {
                                            H.set(i, j, this.QR.get(i, j));
                                        }
                                        else {
                                            H.set(i, j, 0.0);
                                        }
                                    }
                                }
                                return H;
                            };
                            QR.prototype.getR = function () {
                                var R = new org.mwg.ml.common.matrix.Matrix(null, this.n, this.n);
                                for (var i = 0; i < this.n; i++) {
                                    for (var j = 0; j < this.n; j++) {
                                        if (i < j) {
                                            R.set(i, j, this.QR.get(i, j));
                                        }
                                        else {
                                            if (i == j) {
                                                R.set(i, j, this.Rdiag[i]);
                                            }
                                            else {
                                                R.set(i, j, 0.0);
                                            }
                                        }
                                    }
                                }
                                return R;
                            };
                            QR.prototype.getQ = function () {
                                var Q = new org.mwg.ml.common.matrix.Matrix(null, this.m, this.n);
                                for (var k = this.n - 1; k >= 0; k--) {
                                    for (var i = 0; i < this.m; i++) {
                                        Q.set(i, k, 0.0);
                                    }
                                    Q.set(k, k, 1.0);
                                    for (var j = k; j < this.n; j++) {
                                        if (this.QR.get(k, k) != 0) {
                                            var s = 0.0;
                                            for (var i = k; i < this.m; i++) {
                                                s += this.QR.get(i, k) * Q.get(i, j);
                                            }
                                            s = -s / this.QR.get(k, k);
                                            for (var i = k; i < this.m; i++) {
                                                Q.add(i, j, s * this.QR.get(i, k));
                                            }
                                        }
                                    }
                                }
                                return Q;
                            };
                            QR.prototype.solve = function (B) {
                                if (B.rows() != this.m) {
                                    throw new Error("Matrix row dimensions must agree.");
                                }
                                if (!this.isFullRank()) {
                                    throw new Error("Matrix is rank deficient.");
                                }
                                var nx = B.columns();
                                var X = B.clone();
                                for (var k = 0; k < this.n; k++) {
                                    for (var j = 0; j < nx; j++) {
                                        var s = 0.0;
                                        for (var i = k; i < this.m; i++) {
                                            s += this.QR.get(i, k) * X.get(i, j);
                                        }
                                        s = -s / this.QR.get(k, k);
                                        for (var i = k; i < this.m; i++) {
                                            X.add(i, j, s * this.QR.get(i, k));
                                        }
                                    }
                                }
                                for (var k = this.n - 1; k >= 0; k--) {
                                    for (var j = 0; j < nx; j++) {
                                        X.set(k, j, X.get(k, j) / this.Rdiag[k]);
                                    }
                                    for (var i = 0; i < k; i++) {
                                        for (var j = 0; j < nx; j++) {
                                            X.add(i, j, -X.get(k, j) * this.QR.get(i, k));
                                        }
                                    }
                                }
                                return (org.mwg.ml.common.matrix.jamasolver.QR.getMatrix(X, 0, this.n - 1, 0, nx - 1));
                            };
                            QR.getMatrix = function (B, i0, i1, j0, j1) {
                                var X = new org.mwg.ml.common.matrix.Matrix(null, i1 - i0 + 1, j1 - j0 + 1);
                                try {
                                    for (var i = i0; i <= i1; i++) {
                                        for (var j = j0; j <= j1; j++) {
                                            X.set(i - i0, j - j0, B.get(i, j));
                                        }
                                    }
                                }
                                catch ($ex$) {
                                    if ($ex$ instanceof Error) {
                                        var e = $ex$;
                                        throw new Error("Submatrix indices");
                                    }
                                    else {
                                        throw $ex$;
                                    }
                                }
                                return X;
                            };
                            return QR;
                        }());
                        jamasolver.QR = QR;
                        var SVD = (function () {
                            function SVD(Arg) {
                                var A = Arg.clone();
                                this.m = Arg.rows();
                                this.n = Arg.columns();
                                var nu = Math.min(this.m, this.n);
                                this.s = new Float64Array(Math.min(this.m + 1, this.n));
                                this.U = new org.mwg.ml.common.matrix.Matrix(null, this.m, nu);
                                this.V = new org.mwg.ml.common.matrix.Matrix(null, this.n, this.n);
                                var e = new Float64Array(this.n);
                                var work = new Float64Array(this.m);
                                var wantu = true;
                                var wantv = true;
                                var nct = Math.min(this.m - 1, this.n);
                                var nrt = Math.max(0, Math.min(this.n - 2, this.m));
                                for (var k = 0; k < Math.max(nct, nrt); k++) {
                                    if (k < nct) {
                                        this.s[k] = 0;
                                        for (var i = k; i < this.m; i++) {
                                            this.s[k] = org.mwg.ml.common.matrix.jamasolver.Utils.hypot(this.s[k], A.get(i, k));
                                        }
                                        if (this.s[k] != 0.0) {
                                            if (A.get(k, k) < 0.0) {
                                                this.s[k] = -this.s[k];
                                            }
                                            for (var i = k; i < this.m; i++) {
                                                A.set(i, k, A.get(i, k) / this.s[k]);
                                            }
                                            A.add(k, k, 1.0);
                                        }
                                        this.s[k] = -this.s[k];
                                    }
                                    for (var j = k + 1; j < this.n; j++) {
                                        if ((k < nct) && (this.s[k] != 0.0)) {
                                            var t = 0;
                                            for (var i = k; i < this.m; i++) {
                                                t += A.get(i, k) * A.get(i, j);
                                            }
                                            t = -t / A.get(k, k);
                                            for (var i = k; i < this.m; i++) {
                                                A.add(i, j, +t * A.get(i, k));
                                            }
                                        }
                                        e[j] = A.get(k, j);
                                    }
                                    if (wantu && (k < nct)) {
                                        for (var i = k; i < this.m; i++) {
                                            this.U.set(i, k, A.get(i, k));
                                        }
                                    }
                                    if (k < nrt) {
                                        e[k] = 0;
                                        for (var i = k + 1; i < this.n; i++) {
                                            e[k] = org.mwg.ml.common.matrix.jamasolver.Utils.hypot(e[k], e[i]);
                                        }
                                        if (e[k] != 0.0) {
                                            if (e[k + 1] < 0.0) {
                                                e[k] = -e[k];
                                            }
                                            for (var i = k + 1; i < this.n; i++) {
                                                e[i] /= e[k];
                                            }
                                            e[k + 1] += 1.0;
                                        }
                                        e[k] = -e[k];
                                        if ((k + 1 < this.m) && (e[k] != 0.0)) {
                                            for (var i = k + 1; i < this.m; i++) {
                                                work[i] = 0.0;
                                            }
                                            for (var j = k + 1; j < this.n; j++) {
                                                for (var i = k + 1; i < this.m; i++) {
                                                    work[i] += e[j] * A.get(i, j);
                                                }
                                            }
                                            for (var j = k + 1; j < this.n; j++) {
                                                var t = -e[j] / e[k + 1];
                                                for (var i = k + 1; i < this.m; i++) {
                                                    A.add(i, j, t * work[i]);
                                                }
                                            }
                                        }
                                        if (wantv) {
                                            for (var i = k + 1; i < this.n; i++) {
                                                this.V.set(i, k, e[i]);
                                            }
                                        }
                                    }
                                }
                                var p = Math.min(this.n, this.m + 1);
                                if (nct < this.n) {
                                    this.s[nct] = A.get(nct, nct);
                                }
                                if (this.m < p) {
                                    this.s[p - 1] = 0.0;
                                }
                                if (nrt + 1 < p) {
                                    e[nrt] = A.get(nrt, p - 1);
                                }
                                e[p - 1] = 0.0;
                                if (wantu) {
                                    for (var j = nct; j < nu; j++) {
                                        for (var i = 0; i < this.m; i++) {
                                            this.U.set(i, j, 0.0);
                                        }
                                        this.U.set(j, j, 1.0);
                                    }
                                    for (var k = nct - 1; k >= 0; k--) {
                                        if (this.s[k] != 0.0) {
                                            for (var j = k + 1; j < nu; j++) {
                                                var t = 0;
                                                for (var i = k; i < this.m; i++) {
                                                    t += this.U.get(i, k) * this.U.get(i, j);
                                                }
                                                t = -t / this.U.get(k, k);
                                                for (var i = k; i < this.m; i++) {
                                                    this.U.add(i, j, t * this.U.get(i, k));
                                                }
                                            }
                                            for (var i = k; i < this.m; i++) {
                                                this.U.set(i, k, -this.U.get(i, k));
                                            }
                                            this.U.set(k, k, 1.0 + this.U.get(k, k));
                                            for (var i = 0; i < k - 1; i++) {
                                                this.U.set(i, k, 0.0);
                                            }
                                        }
                                        else {
                                            for (var i = 0; i < this.m; i++) {
                                                this.U.set(i, k, 0.0);
                                            }
                                            this.U.set(k, k, 1.0);
                                        }
                                    }
                                }
                                if (wantv) {
                                    for (var k = this.n - 1; k >= 0; k--) {
                                        if ((k < nrt) && (e[k] != 0.0)) {
                                            for (var j = k + 1; j < nu; j++) {
                                                var t = 0;
                                                for (var i = k + 1; i < this.n; i++) {
                                                    t += this.V.get(i, k) * this.V.get(i, j);
                                                }
                                                t = -t / this.V.get(k + 1, k);
                                                for (var i = k + 1; i < this.n; i++) {
                                                    this.V.add(i, j, t * this.V.get(i, k));
                                                }
                                            }
                                        }
                                        for (var i = 0; i < this.n; i++) {
                                            this.V.set(i, k, 0.0);
                                        }
                                        this.V.set(k, k, 1.0);
                                    }
                                }
                                var pp = p - 1;
                                var iter = 0;
                                var eps = Math.pow(2.0, -52.0);
                                var tiny = Math.pow(2.0, -966.0);
                                while (p > 0) {
                                    var k, kase;
                                    for (k = p - 2; k >= -1; k--) {
                                        if (k == -1) {
                                            break;
                                        }
                                        if (Math.abs(e[k]) <= tiny + eps * (Math.abs(this.s[k]) + Math.abs(this.s[k + 1]))) {
                                            e[k] = 0.0;
                                            break;
                                        }
                                    }
                                    if (k == p - 2) {
                                        kase = 4;
                                    }
                                    else {
                                        var ks;
                                        for (ks = p - 1; ks >= k; ks--) {
                                            if (ks == k) {
                                                break;
                                            }
                                            var t = (ks != p ? Math.abs(e[ks]) : 0.) + (ks != k + 1 ? Math.abs(e[ks - 1]) : 0.);
                                            if (Math.abs(this.s[ks]) <= tiny + eps * t) {
                                                this.s[ks] = 0.0;
                                                break;
                                            }
                                        }
                                        if (ks == k) {
                                            kase = 3;
                                        }
                                        else {
                                            if (ks == p - 1) {
                                                kase = 1;
                                            }
                                            else {
                                                kase = 2;
                                                k = ks;
                                            }
                                        }
                                    }
                                    k++;
                                    switch (kase) {
                                        case 1:
                                            var f = e[p - 2];
                                            e[p - 2] = 0.0;
                                            for (var j = p - 2; j >= k; j--) {
                                                var t = org.mwg.ml.common.matrix.jamasolver.Utils.hypot(this.s[j], f);
                                                var cs = this.s[j] / t;
                                                var sn = f / t;
                                                this.s[j] = t;
                                                if (j != k) {
                                                    f = -sn * e[j - 1];
                                                    e[j - 1] = cs * e[j - 1];
                                                }
                                                if (wantv) {
                                                    for (var i = 0; i < this.n; i++) {
                                                        t = cs * this.V.get(i, j) + sn * this.V.get(i, p - 1);
                                                        this.V.set(i, p - 1, -sn * this.V.get(i, j) + cs * this.V.get(i, p - 1));
                                                        this.V.set(i, j, t);
                                                    }
                                                }
                                            }
                                            break;
                                        case 2:
                                            var f = e[k - 1];
                                            e[k - 1] = 0.0;
                                            for (var j = k; j < p; j++) {
                                                var t = org.mwg.ml.common.matrix.jamasolver.Utils.hypot(this.s[j], f);
                                                var cs = this.s[j] / t;
                                                var sn = f / t;
                                                this.s[j] = t;
                                                f = -sn * e[j];
                                                e[j] = cs * e[j];
                                                if (wantu) {
                                                    for (var i = 0; i < this.m; i++) {
                                                        t = cs * this.U.get(i, j) + sn * this.U.get(i, k - 1);
                                                        this.U.set(i, k - 1, -sn * this.U.get(i, j) + cs * this.U.get(i, k - 1));
                                                        this.U.set(i, j, t);
                                                    }
                                                }
                                            }
                                            break;
                                        case 3:
                                            var scale = Math.max(Math.max(Math.max(Math.max(Math.abs(this.s[p - 1]), Math.abs(this.s[p - 2])), Math.abs(e[p - 2])), Math.abs(this.s[k])), Math.abs(e[k]));
                                            var sp = this.s[p - 1] / scale;
                                            var spm1 = this.s[p - 2] / scale;
                                            var epm1 = e[p - 2] / scale;
                                            var sk = this.s[k] / scale;
                                            var ek = e[k] / scale;
                                            var b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
                                            var c = (sp * epm1) * (sp * epm1);
                                            var shift = 0.0;
                                            if ((b != 0.0) || (c != 0.0)) {
                                                shift = Math.sqrt(b * b + c);
                                                if (b < 0.0) {
                                                    shift = -shift;
                                                }
                                                shift = c / (b + shift);
                                            }
                                            var f = (sk + sp) * (sk - sp) + shift;
                                            var g = sk * ek;
                                            for (var j = k; j < p - 1; j++) {
                                                var t = org.mwg.ml.common.matrix.jamasolver.Utils.hypot(f, g);
                                                var cs = f / t;
                                                var sn = g / t;
                                                if (j != k) {
                                                    e[j - 1] = t;
                                                }
                                                f = cs * this.s[j] + sn * e[j];
                                                e[j] = cs * e[j] - sn * this.s[j];
                                                g = sn * this.s[j + 1];
                                                this.s[j + 1] = cs * this.s[j + 1];
                                                if (wantv) {
                                                    for (var i = 0; i < this.n; i++) {
                                                        t = cs * this.V.get(i, j) + sn * this.V.get(i, j + 1);
                                                        this.V.set(i, j + 1, -sn * this.V.get(i, j) + cs * this.V.get(i, j + 1));
                                                        this.V.set(i, j, t);
                                                    }
                                                }
                                                t = org.mwg.ml.common.matrix.jamasolver.Utils.hypot(f, g);
                                                cs = f / t;
                                                sn = g / t;
                                                this.s[j] = t;
                                                f = cs * e[j] + sn * this.s[j + 1];
                                                this.s[j + 1] = -sn * e[j] + cs * this.s[j + 1];
                                                g = sn * e[j + 1];
                                                e[j + 1] = cs * e[j + 1];
                                                if (wantu && (j < this.m - 1)) {
                                                    for (var i = 0; i < this.m; i++) {
                                                        t = cs * this.U.get(i, j) + sn * this.U.get(i, j + 1);
                                                        this.U.set(i, j + 1, -sn * this.U.get(i, j) + cs * this.U.get(i, j + 1));
                                                        this.U.set(i, j, t);
                                                    }
                                                }
                                            }
                                            e[p - 2] = f;
                                            iter = iter + 1;
                                            break;
                                        case 4:
                                            if (this.s[k] <= 0.0) {
                                                this.s[k] = (this.s[k] < 0.0 ? -this.s[k] : 0.0);
                                                if (wantv) {
                                                    for (var i = 0; i <= pp; i++) {
                                                        this.V.set(i, k, -this.V.get(i, k));
                                                    }
                                                }
                                            }
                                            while (k < pp) {
                                                if (this.s[k] >= this.s[k + 1]) {
                                                    break;
                                                }
                                                var t = this.s[k];
                                                this.s[k] = this.s[k + 1];
                                                this.s[k + 1] = t;
                                                if (wantv && (k < this.n - 1)) {
                                                    for (var i = 0; i < this.n; i++) {
                                                        t = this.V.get(i, k + 1);
                                                        this.V.set(i, k + 1, this.V.get(i, k));
                                                        this.V.set(i, k, t);
                                                    }
                                                }
                                                if (wantu && (k < this.m - 1)) {
                                                    for (var i = 0; i < this.m; i++) {
                                                        t = this.U.get(i, k + 1);
                                                        this.U.set(i, k + 1, this.U.get(i, k));
                                                        this.U.set(i, k, t);
                                                    }
                                                }
                                                k++;
                                            }
                                            iter = 0;
                                            p--;
                                            break;
                                    }
                                }
                            }
                            SVD.prototype.factor = function (A, workInPlace) {
                                return new org.mwg.ml.common.matrix.jamasolver.SVD(A);
                            };
                            SVD.prototype.getU = function () {
                                return this.U;
                            };
                            SVD.prototype.getVt = function () {
                                return org.mwg.ml.common.matrix.Matrix.transpose(this.getV());
                            };
                            SVD.prototype.getV = function () {
                                return this.V;
                            };
                            SVD.prototype.getSingularValues = function () {
                                return this.s;
                            };
                            SVD.prototype.getSMatrix = function () {
                                var X = new org.mwg.ml.common.matrix.Matrix(null, Math.min(this.m, this.n), this.n);
                                for (var i = 0; i < this.s.length; i++) {
                                    if (i < this.m && i < this.n) {
                                        X.set(i, i, this.s[i]);
                                    }
                                }
                                return X;
                            };
                            SVD.prototype.getS = function () {
                                return this.s;
                            };
                            SVD.prototype.norm2 = function () {
                                return this.s[0];
                            };
                            SVD.prototype.cond = function () {
                                return this.s[0] / this.s[Math.min(this.m, this.n) - 1];
                            };
                            SVD.prototype.rank = function () {
                                var eps = Math.pow(2.0, -52.0);
                                var tol = Math.max(this.m, this.n) * this.s[0] * eps;
                                var r = 0;
                                for (var i = 0; i < this.s.length; i++) {
                                    if (this.s[i] > tol) {
                                        r++;
                                    }
                                }
                                return r;
                            };
                            SVD.serialVersionUID = 1;
                            return SVD;
                        }());
                        jamasolver.SVD = SVD;
                        var Utils = (function () {
                            function Utils() {
                            }
                            Utils.hypot = function (a, b) {
                                var r;
                                if (Math.abs(a) > Math.abs(b)) {
                                    r = b / a;
                                    r = Math.abs(a) * Math.sqrt(1 + r * r);
                                }
                                else {
                                    if (b != 0) {
                                        r = a / b;
                                        r = Math.abs(b) * Math.sqrt(1 + r * r);
                                    }
                                    else {
                                        r = 0.0;
                                    }
                                }
                                return r;
                            };
                            return Utils;
                        }());
                        jamasolver.Utils = Utils;
                    })(jamasolver = matrix.jamasolver || (matrix.jamasolver = {}));
                    var Matrix = (function () {
                        function Matrix(backend, p_nbRows, p_nbColumns) {
                            this._nbRows = p_nbRows;
                            this._nbColumns = p_nbColumns;
                            if (backend != null) {
                                this._data = backend;
                            }
                            else {
                                this._data = new Float64Array(this._nbRows * this._nbColumns);
                            }
                        }
                        Matrix.compare = function (a, b, eps) {
                            if (a == null || b == null) {
                                return false;
                            }
                            for (var i = 0; i < a.length; i++) {
                                if (Math.abs(a[i] - b[i]) > eps) {
                                    return false;
                                }
                            }
                            return true;
                        };
                        Matrix.compareArray = function (a, b, eps) {
                            if (a == null || b == null) {
                                return false;
                            }
                            for (var i = 0; i < a.length; i++) {
                                if (!org.mwg.ml.common.matrix.Matrix.compare(a[i], b[i], eps)) {
                                    return false;
                                }
                            }
                            return true;
                        };
                        Matrix.prototype.data = function () {
                            return this._data;
                        };
                        Matrix.prototype.exportRowMatrix = function () {
                            var res = new Float64Array(this._data.length);
                            var k = 0;
                            for (var i = 0; i < this._nbRows; i++) {
                                for (var j = 0; j < this._nbColumns; j++) {
                                    res[k] = this.get(i, j);
                                    k++;
                                }
                            }
                            return res;
                        };
                        Matrix.prototype.importRowMatrix = function (rowdata, rows, columns) {
                            var res = new org.mwg.ml.common.matrix.Matrix(null, rows, columns);
                            var k = 0;
                            for (var i = 0; i < this._nbRows; i++) {
                                for (var j = 0; j < this._nbColumns; j++) {
                                    res.set(i, j, rowdata[k]);
                                    k++;
                                }
                            }
                            return res;
                        };
                        Matrix.prototype.setData = function (data) {
                            java.lang.System.arraycopy(data, 0, this._data, 0, data.length);
                        };
                        Matrix.prototype.rows = function () {
                            return this._nbRows;
                        };
                        Matrix.prototype.columns = function () {
                            return this._nbColumns;
                        };
                        Matrix.prototype.get = function (rowIndex, columnIndex) {
                            return this._data[rowIndex + columnIndex * this._nbRows];
                        };
                        Matrix.prototype.set = function (rowIndex, columnIndex, value) {
                            this._data[rowIndex + columnIndex * this._nbRows] = value;
                            return value;
                        };
                        Matrix.prototype.add = function (rowIndex, columnIndex, value) {
                            return this.set(rowIndex, columnIndex, this.get(rowIndex, columnIndex) + value);
                        };
                        Matrix.prototype.setAll = function (value) {
                            for (var i = 0; i < this._nbColumns * this._nbRows; i++) {
                                this._data[i] = value;
                            }
                        };
                        Matrix.prototype.getAtIndex = function (index) {
                            return this._data[index];
                        };
                        Matrix.prototype.setAtIndex = function (index, value) {
                            this._data[index] = value;
                            return value;
                        };
                        Matrix.prototype.addAtIndex = function (index, value) {
                            this._data[index] += value;
                            return this._data[index];
                        };
                        Matrix.prototype.clone = function () {
                            var newback = new Float64Array(this._data.length);
                            java.lang.System.arraycopy(this._data, 0, newback, 0, this._data.length);
                            var res = new org.mwg.ml.common.matrix.Matrix(newback, this._nbRows, this._nbColumns);
                            return res;
                        };
                        Matrix.defaultEngine = function () {
                            if (Matrix._defaultEngine == null) {
                                Matrix._defaultEngine = new org.mwg.ml.common.matrix.jamasolver.JamaMatrixEngine();
                            }
                            return Matrix._defaultEngine;
                        };
                        Matrix.multiply = function (matA, matB) {
                            return org.mwg.ml.common.matrix.Matrix.defaultEngine().multiplyTransposeAlphaBeta(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE, 1, matA, org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE, 1, matB);
                        };
                        Matrix.multiplyTransposeAlphaBeta = function (transA, alpha, matA, transB, beta, matB) {
                            return org.mwg.ml.common.matrix.Matrix.defaultEngine().multiplyTransposeAlphaBeta(transA, alpha, matA, transB, beta, matB);
                        };
                        Matrix.invert = function (mat, invertInPlace) {
                            return org.mwg.ml.common.matrix.Matrix.defaultEngine().invert(mat, invertInPlace);
                        };
                        Matrix.pinv = function (mat, invertInPlace) {
                            return org.mwg.ml.common.matrix.Matrix.defaultEngine().pinv(mat, invertInPlace);
                        };
                        Matrix.leadingDimension = function (matA) {
                            return Math.max(matA.columns(), matA.rows());
                        };
                        Matrix.random = function (rows, columns, min, max) {
                            var res = new org.mwg.ml.common.matrix.Matrix(null, rows, columns);
                            var rand = new java.util.Random();
                            for (var i = 0; i < rows * columns; i++) {
                                res.setAtIndex(i, rand.nextDouble() * (max - min) + min);
                            }
                            return res;
                        };
                        Matrix.scale = function (alpha, matA) {
                            if (alpha == 0) {
                                matA.setAll(0);
                                return;
                            }
                            for (var i = 0; i < matA.rows() * matA.columns(); i++) {
                                matA.setAtIndex(i, alpha * matA.getAtIndex(i));
                            }
                        };
                        Matrix.transpose = function (matA) {
                            var result = new org.mwg.ml.common.matrix.Matrix(null, matA.columns(), matA.rows());
                            var TRANSPOSE_SWITCH = 375;
                            if (matA.columns() == matA.rows()) {
                                org.mwg.ml.common.matrix.Matrix.transposeSquare(matA, result);
                            }
                            else {
                                if (matA.columns() > TRANSPOSE_SWITCH && matA.rows() > TRANSPOSE_SWITCH) {
                                    org.mwg.ml.common.matrix.Matrix.transposeBlock(matA, result);
                                }
                                else {
                                    org.mwg.ml.common.matrix.Matrix.transposeStandard(matA, result);
                                }
                            }
                            return result;
                        };
                        Matrix.transposeSquare = function (matA, result) {
                            var index = 1;
                            var indexEnd = matA.columns();
                            for (var i = 0; i < matA.rows(); i++) {
                                var indexOther = (i + 1) * matA.columns() + i;
                                var n = i * (matA.columns() + 1);
                                result.setAtIndex(n, matA.getAtIndex(n));
                                for (; index < indexEnd; index++) {
                                    result.setAtIndex(index, matA.getAtIndex(indexOther));
                                    result.setAtIndex(indexOther, matA.getAtIndex(index));
                                    indexOther += matA.columns();
                                }
                                index += i + 2;
                                indexEnd += matA.columns();
                            }
                        };
                        Matrix.transposeStandard = function (matA, result) {
                            var index = 0;
                            for (var i = 0; i < result.columns(); i++) {
                                var index2 = i;
                                var end = index + result.rows();
                                while (index < end) {
                                    result.setAtIndex(index++, matA.getAtIndex(index2));
                                    index2 += matA.rows();
                                }
                            }
                        };
                        Matrix.transposeBlock = function (matA, result) {
                            var BLOCK_WIDTH = 60;
                            for (var j = 0; j < matA.columns(); j += BLOCK_WIDTH) {
                                var blockWidth = Math.min(BLOCK_WIDTH, matA.columns() - j);
                                var indexSrc = j * matA.rows();
                                var indexDst = j;
                                for (var i = 0; i < matA.rows(); i += BLOCK_WIDTH) {
                                    var blockHeight = Math.min(BLOCK_WIDTH, matA.rows() - i);
                                    var indexSrcEnd = indexSrc + blockHeight;
                                    for (; indexSrc < indexSrcEnd; indexSrc++) {
                                        var colSrc = indexSrc;
                                        var colDst = indexDst;
                                        var end = colDst + blockWidth;
                                        for (; colDst < end; colDst++) {
                                            result.setAtIndex(colDst, matA.getAtIndex(colSrc));
                                            colSrc += matA.rows();
                                        }
                                        indexDst += result.rows();
                                    }
                                }
                            }
                        };
                        Matrix.prototype.saveToState = function () {
                            var res = new Float64Array(this._data.length + 2);
                            res[0] = this._nbRows;
                            res[1] = this._nbColumns;
                            java.lang.System.arraycopy(this._data, 0, res, 2, this._data.length);
                            return res;
                        };
                        Matrix.loadFromState = function (o) {
                            var res = o;
                            var data = new Float64Array(res.length - 2);
                            java.lang.System.arraycopy(res, 2, data, 0, data.length);
                            return new org.mwg.ml.common.matrix.Matrix(data, res[0], res[1]);
                        };
                        Matrix.createIdentity = function (rows, columns) {
                            var ret = new org.mwg.ml.common.matrix.Matrix(null, rows, columns);
                            var width = Math.min(rows, columns);
                            for (var i = 0; i < width; i++) {
                                ret.set(i, i, 1);
                            }
                            return ret;
                        };
                        Matrix.compareMatrix = function (matA, matB) {
                            var err = 0;
                            for (var i = 0; i < matA.rows(); i++) {
                                for (var j = 0; j < matA.columns(); j++) {
                                    if (err < Math.abs(matA.get(i, j) - matB.get(i, j))) {
                                        err = Math.abs(matA.get(i, j) - matB.get(i, j));
                                    }
                                }
                            }
                            return err;
                        };
                        Matrix.testDimensionsAB = function (transA, transB, matA, matB) {
                            if (transA.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                if (transB.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                    return (matA.columns() == matB.rows());
                                }
                                else {
                                    return (matA.columns() == matB.columns());
                                }
                            }
                            else {
                                if (transB.equals(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE)) {
                                    return (matA.rows() == matB.rows());
                                }
                                else {
                                    return (matA.rows() == matB.columns());
                                }
                            }
                        };
                        Matrix.identity = function (rows, columns) {
                            var res = new org.mwg.ml.common.matrix.Matrix(null, rows, columns);
                            for (var i = 0; i < Math.max(rows, columns); i++) {
                                res.set(i, i, 1.0);
                            }
                            return res;
                        };
                        Matrix._defaultEngine = null;
                        return Matrix;
                    }());
                    matrix.Matrix = Matrix;
                    var operation;
                    (function (operation) {
                        var Gaussian1D = (function () {
                            function Gaussian1D() {
                            }
                            Gaussian1D.getCovariance = function (sum, sumSq, total) {
                                return (sumSq - (sum * sum) / total) / (total - 1);
                            };
                            Gaussian1D.getDensity = function (sum, sumSq, total, feature) {
                                if (total < 2) {
                                    return 0;
                                }
                                var avg = sum / total;
                                var cov = org.mwg.ml.common.matrix.operation.Gaussian1D.getCovariance(sum, sumSq, total);
                                return 1 / Math.sqrt(2 * Math.PI * cov) * Math.exp(-(feature - avg) * (feature - avg) / (2 * cov));
                            };
                            Gaussian1D.getDensityArray = function (sum, sumSq, total, feature) {
                                if (total < 2) {
                                    return null;
                                }
                                var avg = sum / total;
                                var cov = org.mwg.ml.common.matrix.operation.Gaussian1D.getCovariance(sum, sumSq, total);
                                var exp = 1 / Math.sqrt(2 * Math.PI * cov);
                                var proba = new Float64Array(feature.length);
                                for (var i = 0; i < feature.length; i++) {
                                    proba[i] = exp * Math.exp(-(feature[i] - avg) * (feature[i] - avg) / (2 * cov));
                                }
                                return proba;
                            };
                            return Gaussian1D;
                        }());
                        operation.Gaussian1D = Gaussian1D;
                        var MultivariateNormalDistribution = (function () {
                            function MultivariateNormalDistribution(means, cov, allowSingular) {
                                this.means = means;
                                if (cov != null) {
                                    this.covariance = cov;
                                    this.covDiag = new Float64Array(cov.rows());
                                    for (var i = 0; i < this.covDiag.length; i++) {
                                        this.covDiag[i] = cov.get(i, i);
                                    }
                                    this.pinvsvd = new org.mwg.ml.common.matrix.operation.PInvSVD();
                                    this.pinvsvd.factor(this.covariance, false);
                                    this.inv = this.pinvsvd.getPInv();
                                    this.det = this.pinvsvd.getDeterminant();
                                    this.rank = this.pinvsvd.getRank();
                                    if (!allowSingular && this.rank < cov.rows()) {
                                        this.covariance = cov.clone();
                                        var temp = new Float64Array(this.covDiag.length);
                                        for (var i = 0; i < this.covDiag.length; i++) {
                                            temp[i] = Math.sqrt(this.covDiag[i]);
                                        }
                                        for (var i = 0; i < this.covDiag.length; i++) {
                                            for (var j = i + 1; j < this.covDiag.length; j++) {
                                                var d = this.covariance.get(i, j) - 0.0001 * temp[i] * temp[j];
                                                this.covariance.set(i, j, d);
                                                this.covariance.set(j, i, d);
                                            }
                                        }
                                        this.pinvsvd = new org.mwg.ml.common.matrix.operation.PInvSVD();
                                        this.pinvsvd.factor(this.covariance, false);
                                        this.inv = this.pinvsvd.getPInv();
                                        this.det = this.pinvsvd.getDeterminant();
                                        this.rank = this.pinvsvd.getRank();
                                    }
                                }
                            }
                            MultivariateNormalDistribution.prototype.getMin = function () {
                                return this.min;
                            };
                            MultivariateNormalDistribution.prototype.getMax = function () {
                                return this.max;
                            };
                            MultivariateNormalDistribution.prototype.getAvg = function () {
                                return this.means;
                            };
                            MultivariateNormalDistribution.prototype.getCovDiag = function () {
                                return this.covDiag;
                            };
                            MultivariateNormalDistribution.prototype.setMin = function (min) {
                                this.min = min;
                            };
                            MultivariateNormalDistribution.prototype.setMax = function (max) {
                                this.max = max;
                            };
                            MultivariateNormalDistribution.getCovariance = function (sum, sumsquares, total) {
                                if (total < 2) {
                                    return null;
                                }
                                var features = sum.length;
                                var avg = new Float64Array(features);
                                for (var i = 0; i < features; i++) {
                                    avg[i] = sum[i] / total;
                                }
                                var covariances = new Float64Array(features * features);
                                var correction = total;
                                correction = correction / (total - 1);
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    for (var j = i; j < features; j++) {
                                        covariances[i * features + j] = (sumsquares[count] / total - avg[i] * avg[j]) * correction;
                                        covariances[j * features + i] = covariances[i * features + j];
                                        count++;
                                    }
                                }
                                var cov = new org.mwg.ml.common.matrix.Matrix(covariances, features, features);
                                return cov;
                            };
                            MultivariateNormalDistribution.getDistribution = function (sum, sumsquares, total, allowSingular) {
                                if (total < 2) {
                                    return null;
                                }
                                var features = sum.length;
                                var avg = new Float64Array(features);
                                for (var i = 0; i < features; i++) {
                                    avg[i] = sum[i] / total;
                                }
                                var covariances = new Float64Array(features * features);
                                var correction = total;
                                correction = correction / (total - 1);
                                var count = 0;
                                for (var i = 0; i < features; i++) {
                                    for (var j = i; j < features; j++) {
                                        covariances[i * features + j] = (sumsquares[count] / total - avg[i] * avg[j]) * correction;
                                        covariances[j * features + i] = covariances[i * features + j];
                                        count++;
                                    }
                                }
                                var cov = new org.mwg.ml.common.matrix.Matrix(covariances, features, features);
                                return new org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution(avg, cov, allowSingular);
                            };
                            MultivariateNormalDistribution.prototype.density = function (features, normalizeOnAvg) {
                                if (normalizeOnAvg) {
                                    return this.getExponentTerm(features);
                                }
                                else {
                                    return Math.pow(2 * Math.PI, -0.5 * this.rank) * Math.pow(this.det, -0.5) * this.getExponentTerm(features);
                                }
                            };
                            MultivariateNormalDistribution.prototype.getExponentTerm = function (features) {
                                var f = new Float64Array(features.length);
                                java.lang.System.arraycopy(features, 0, f, 0, features.length);
                                for (var i = 0; i < features.length; i++) {
                                    f[i] = f[i] - this.means[i];
                                }
                                var ft = new org.mwg.ml.common.matrix.Matrix(f, 1, f.length);
                                var ftt = new org.mwg.ml.common.matrix.Matrix(f, f.length, 1);
                                var res = org.mwg.ml.common.matrix.Matrix.multiply(ft, this.inv);
                                var res2 = org.mwg.ml.common.matrix.Matrix.multiply(res, ftt);
                                var d = Math.exp(-0.5 * res2.get(0, 0));
                                return d;
                            };
                            MultivariateNormalDistribution.prototype.clone = function (avg) {
                                var res = new org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution(avg, null, false);
                                res.pinvsvd = this.pinvsvd;
                                res.inv = this.inv;
                                res.det = this.det;
                                res.rank = this.rank;
                                res.covDiag = this.covDiag;
                                return res;
                            };
                            return MultivariateNormalDistribution;
                        }());
                        operation.MultivariateNormalDistribution = MultivariateNormalDistribution;
                        var PInvSVD = (function () {
                            function PInvSVD() {
                            }
                            PInvSVD.prototype.getRank = function () {
                                return this.rank;
                            };
                            PInvSVD.prototype.getDeterminant = function () {
                                return this.det;
                            };
                            PInvSVD.prototype.factor = function (A, invertInPlace) {
                                this._svd = org.mwg.ml.common.matrix.Matrix.defaultEngine().decomposeSVD(A, invertInPlace);
                                var svd = new Array(3);
                                svd[0] = this._svd.getU();
                                svd[1] = this._svd.getSMatrix();
                                svd[2] = this._svd.getVt();
                                var V = this._svd.getVt();
                                this.S = this._svd.getSMatrix().clone();
                                var maxSingular = 0;
                                var dim = Math.min(this.S.columns(), this.S.rows());
                                for (var i = 0; i < dim; i++) {
                                    if (this.S.get(i, i) > maxSingular) {
                                        maxSingular = this.S.get(i, i);
                                    }
                                }
                                var tau = Math.pow(2, -46) * Math.max(A.columns(), A.rows()) * maxSingular;
                                this.rank = 0;
                                this.det = 1;
                                if (maxSingular != 0.0) {
                                    for (var i = 0; i < dim; i++) {
                                        var s = this.S.get(i, i);
                                        if (s < tau) {
                                            this.S.set(i, i, 0);
                                        }
                                        else {
                                            this.S.set(i, i, 1 / s);
                                            this.det = this.det * s;
                                            this.rank++;
                                        }
                                    }
                                }
                                var temp = org.mwg.ml.common.matrix.Matrix.multiplyTransposeAlphaBeta(org.mwg.ml.common.matrix.TransposeType.TRANSPOSE, 1, V, org.mwg.ml.common.matrix.TransposeType.TRANSPOSE, 1, this.S);
                                this.pinv = org.mwg.ml.common.matrix.Matrix.multiplyTransposeAlphaBeta(org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE, 1, temp, org.mwg.ml.common.matrix.TransposeType.TRANSPOSE, 1, this._svd.getU());
                                return this;
                            };
                            PInvSVD.prototype.getSvd = function () {
                                return this._svd;
                            };
                            PInvSVD.prototype.getInvDeterminant = function () {
                                return this.S;
                            };
                            PInvSVD.prototype.getPInv = function () {
                                return this.pinv;
                            };
                            return PInvSVD;
                        }());
                        operation.PInvSVD = PInvSVD;
                        var PolynomialFit = (function () {
                            function PolynomialFit(degree) {
                                this.degree = 0;
                                this.degree = degree;
                            }
                            PolynomialFit.prototype.getCoef = function () {
                                return this.coef.data();
                            };
                            PolynomialFit.prototype.fit = function (samplePoints, observations) {
                                var y = new org.mwg.ml.common.matrix.Matrix(observations, observations.length, 1);
                                var a = new org.mwg.ml.common.matrix.Matrix(null, y.rows(), this.degree + 1);
                                for (var i = 0; i < observations.length; i++) {
                                    var obs = 1;
                                    for (var j = 0; j < this.degree + 1; j++) {
                                        a.set(i, j, obs);
                                        obs *= samplePoints[i];
                                    }
                                }
                                this.coef = org.mwg.ml.common.matrix.Matrix.defaultEngine().solveQR(a, y, true, org.mwg.ml.common.matrix.TransposeType.NOTRANSPOSE);
                            };
                            PolynomialFit.extrapolate = function (time, weights) {
                                var result = 0;
                                var power = 1;
                                for (var j = 0; j < weights.length; j++) {
                                    result += weights[j] * power;
                                    power = power * time;
                                }
                                return result;
                            };
                            return PolynomialFit;
                        }());
                        operation.PolynomialFit = PolynomialFit;
                    })(operation = matrix.operation || (matrix.operation = {}));
                    var TransposeType = (function () {
                        function TransposeType() {
                        }
                        TransposeType.prototype.equals = function (other) {
                            return this == other;
                        };
                        TransposeType.values = function () {
                            return TransposeType._TransposeTypeVALUES;
                        };
                        TransposeType.NOTRANSPOSE = new TransposeType();
                        TransposeType.TRANSPOSE = new TransposeType();
                        TransposeType._TransposeTypeVALUES = [
                            TransposeType.NOTRANSPOSE,
                            TransposeType.TRANSPOSE
                        ];
                        return TransposeType;
                    }());
                    matrix.TransposeType = TransposeType;
                })(matrix = common.matrix || (common.matrix = {}));
                var NDimentionalArray = (function () {
                    function NDimentionalArray() {
                    }
                    NDimentionalArray.prototype.get = function (indices) {
                        return 0;
                    };
                    NDimentionalArray.prototype.set = function (indices, value) { };
                    NDimentionalArray.prototype.add = function (indices, value) {
                        this.set(indices, this.get(indices) + value);
                    };
                    return NDimentionalArray;
                }());
                common.NDimentionalArray = NDimentionalArray;
            })(common = ml.common || (ml.common = {}));
        })(ml = mwg.ml || (mwg.ml = {}));
    })(mwg = org.mwg || (org.mwg = {}));
})(org || (org = {}));
//# sourceMappingURL=mwg.ml.js.map