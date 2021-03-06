package org.wartremover
package warts

import reflect.NameTransformer

object Equals extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val Equals: TermName = NameTransformer.encode("==")
    val NotEquals: TermName = NameTransformer.encode("!=")

    new Traverser {
      override def traverse(tree: Tree) = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>

          case Function(_, body) => traverse(body)

          case _ if isSynthetic(u)(tree) =>

          case Apply(Select(lhs, Equals), _) =>
            error(u)(tree.pos, "== is disabled - use === or equivalent instead")

          case Apply(Select(lhs, NotEquals), _) =>
            error(u)(tree.pos, "!= is disabled - use =/= or equivalent instead")

          case _ => super.traverse(tree)

        }
      }
    }
  }
}
