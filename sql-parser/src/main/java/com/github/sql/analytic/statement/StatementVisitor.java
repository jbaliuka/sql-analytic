/* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.github.sql.analytic.statement;

import com.github.sql.analytic.statement.create.table.CreateTable;
import com.github.sql.analytic.statement.create.view.CreateView;
import com.github.sql.analytic.statement.delete.Delete;
import com.github.sql.analytic.statement.drop.Drop;
import com.github.sql.analytic.statement.insert.Insert;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.statement.replace.Replace;
import com.github.sql.analytic.statement.select.Select;
import com.github.sql.analytic.statement.truncate.Truncate;
import com.github.sql.analytic.statement.update.Update;



public interface StatementVisitor {
	
	public void visit(Select select);
	public void visit(Delete delete);
	public void visit(Update update);
	public void visit(Insert insert);
	public void visit(Replace replace);
	public void visit(Drop drop);
	public void visit(Truncate truncate);
	public void visit(CreateTable createTable);
	public void visit(CreateView createView);
	public void visit(CreatePolicy policy);
	public void visit(Cursor cursor);
	
	

}
