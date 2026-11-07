/**
 * Service Layer - Business Logic, Domain Rules, and Transactional Boundaries.
 *
 * <h2>Transaction Rollback Semantics:</h2>
 * <p>Spring's {@code @Transactional} rolls back automatically only when an unchecked
 * exception ({@link java.lang.RuntimeException} or {@link java.lang.Error}) escapes
 * the transactional method. Returning a {@link fun.trackmoney.common.result.Result.Err}
 * is treated as a normal method return, meaning Spring will commit any database writes that
 * occurred before the {@code Result.err(...)} was returned.</p>
 *
 * <h3>Conventions:</h3>
 * <ul>
 *   <li><b>Pre-Write Validations:</b> When a domain validation fails before any database
 *       writes occur, returning {@code Result.err(...)} is safe and encouraged.</li>
 *   <li><b>Post-Write Errors:</b> When an error condition is detected after database writes
 *       have begun, the service must ensure atomicity using one of:
 *       <ol>
 *         <li><b>Domain Exceptions (Recommended):</b> Throw an unchecked exception
 *             (e.g., {@code AccountNotFoundException}), which causes Spring to roll back
 *             the transaction automatically and is mapped by the global exception handler.</li>
 *         <li><b>Programmatic Rollback with Result:</b> Use
 *             {@link fun.trackmoney.service.TransactionRollbackHelper#rollbackAndErr}
 *             to explicitly set {@code TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()}
 *             before returning {@code Result.err(...)}.</li>
 *       </ol>
 *   </li>
 * </ul>
 */
package fun.trackmoney.service;
