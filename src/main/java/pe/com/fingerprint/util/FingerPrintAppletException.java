package pe.com.fingerprint.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.apache.commons.lang3.ArrayUtils;

public class FingerPrintAppletException
    extends Exception
{

    /**
     * Unique identifier used to serialize this class.
     */
    private static final long serialVersionUID = 1906998311318776048L;

    /**
     * The instance variable stores the class name where the exception occurs.
     *
     * @see #getClassName()
     */
    private final Class<?> className;

    /**
     * The instance variable stores the id (key) of the exception.
     *
     * @see #getId()
     */
    private final String id;

    /**
     * The instance variable stores the arguments replaced in the error text.
     *
     * @see #getArgs()
     */
    private final Object[] args;

    /**
     * @param _className name of class in which the exception is thrown
     * @param _id id of the exception which is thrown
     * @param _args argument arrays
     */
    public FingerPrintAppletException(final Class<?> _className,
                             final String _id,
                             final Object... _args)
    {
        super("error in " + _className.getName() + "(" + _id + "," + ArrayUtils.toString(_args, "Null ARRAY ") + ")");
        this.id = _id;
        this.className = _className;
        if (_args != null && _args.length > 0 && _args[0] instanceof Throwable) {
            initCause((Throwable) _args[0]);
        }
        this.args = _args;
    }

    /**
     * @param _message message of the exception
     * @param _cause cause
     */
    public FingerPrintAppletException(final String _message,
                             final Throwable _cause)
    {
        super(_message, _cause);
        if (_cause instanceof FingerPrintAppletException) {
            final FingerPrintAppletException cause = (FingerPrintAppletException) _cause;
            this.id = cause.getId();
            this.className = cause.getClassName();
            this.args = cause.getArgs();
        } else {
            this.id = null;
            this.className = null;
            this.args = null;
        }
    }

    /**
     * If a caused exception is a {@link SQLException}, also all next exceptions
     * of the {@link SQLException}'s are printed into the stack trace.
     *
     * @param _stream <code>PrintStream</code> to use for output
     * @see #makeInfo() to get all information about this JXMarketException
     */
    @Override
    public void printStackTrace(final PrintStream _stream)
    {
        _stream.append(makeInfo());
        super.printStackTrace(_stream);
        if ((getCause() != null) && (getCause() instanceof SQLException)) {
            SQLException ex = (SQLException) getCause();
            ex = ex.getNextException();
            while (ex != null) {
                _stream.append("Next SQL Exception is: ");
                ex.printStackTrace(_stream);
                ex = ex.getNextException();
            }
        }
    }

    /**
     * If a caused exception is a {@link SQLException}, also all next exceptions
     * of the {@link SQLException}'s are printed into the stack trace.
     *
     * @param _writer <code>PrintWriter</code> to use for output
     * @see #makeInfo() to get all information about this JXMarketException
     */
    @Override
    public void printStackTrace(final PrintWriter _writer)
    {
        _writer.append(makeInfo());
        if (this.className != null) {
            _writer.append("Thrown within class ").append(this.className.getName()).append('\n');
        }
        super.printStackTrace(_writer);
        if ((getCause() != null) && (getCause() instanceof SQLException)) {
            SQLException ex = (SQLException) getCause();
            ex = ex.getNextException();
            while (ex != null) {
                _writer.append("Next SQL Exception is: ");
                ex.printStackTrace(_writer);
                ex = ex.getNextException();
            }
        }
    }

    /**
     * Prepares a string of all information of this JXMarketException. The
     * returned string includes information about the class which throws this
     * exception, the exception id and all arguments.
     *
     * @return string representation about the JXMarketException
     */
    protected String makeInfo()
    {
        final StringBuilder str = new StringBuilder();
        if (this.className != null) {
            str.append("Thrown within class ").append(this.className.getName()).append('\n');
        }
        if (this.id != null) {
            str.append("Id of Exception is ").append(this.id).append('\n');
        }
        if ((this.args != null) && (this.args.length > 0)) {
            str.append("Arguments are:\n");
            for (Integer index = 0; index < this.args.length; index++) {
                final String arg = (this.args[index] == null)
                                ? "null"
                                : this.args[index].toString();
                str.append("\targs[").append(index.toString()).append("] = '").append(arg).append("'\n");
            }
        }
        return str.toString();
    }

    /**
     * This is the getter method for instance variable {@link #className}.
     *
     * @return value of instance variable {@link #className}
     * @see #className
     */
    public Class<?> getClassName()
    {
        return this.className;
    }

    /**
     * This is the getter method for instance variable {@link #id}.
     *
     * @return value of instance variable {@link #id}
     * @see #id
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * This is the getter method for instance variable {@link #args}.
     *
     * @return value of instance variable {@link #args}
     * @see #args
     */
    public Object[] getArgs()
    {
        return this.args;
    }
}
